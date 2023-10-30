package com.heima.wemedia.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.vos.WmNewsVo;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    /**
     * 条件查询文章列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findList(WmNewsPageReqDto dto) {
        //1.参数检查
        dto.checkParam();

        //2.分页查询
        List<WmNewsVo> wmNewsVoList = baseMapper.findList(dto);
        int count = baseMapper.findListCount(dto);

        //3.结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), count);
        responseResult.setData(wmNewsVoList);
        return responseResult;
    }

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    @Autowired
    private WmNewsTaskService wmNewsTaskService;

    @Override
    public ResponseResult submitNews(WmNewsDto dto) {
        // 1. 保存或修改文章
        WmNews wmNews = new WmNews();
        // 属性拷贝, 属性名称和类型相同才能拷贝
        BeanUtils.copyProperties(dto, wmNews);
        // 封面图片
        if (dto.getImages() != null && dto.getImages().size() > 0) {
            String imageStr = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(imageStr);
        }
        // 如果当前封面类型为自动 -1
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            wmNews.setType(null);
        }
        saveOrUpdateWmNews(wmNews);

        // 2. 判断是否为草稿, 如果为草稿结束当前方法
        if (dto.getStatus().equals(WmNews.Status.NORMAL.getCode())) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 3. 不是草稿, 保存文章内容图片与素材的关系
        // 获取到文章中的图片信息
        List<String> contentImages = ectractUrlInfo(dto.getContent());
        saveRelativeInfoForContent(contentImages, wmNews.getId());

        // 4. 不是草稿, 保存文章封面图片与素材的关系, 如果当前布局为自动, 需要匹配封面图片
        saveRelativeInfoForCover(dto, wmNews, contentImages);

        // 审核文章
        // wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
        wmNewsTaskService.addNewsToTask(wmNews.getId(), wmNews.getPublishTime());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 第一个功能 : 如果当前封面类型为自动, 则设置封面类型的数据
     * 匹配规则:
     * 1. 如果内容图片大于等于1 小于3 单图 type 1
     * 2. 如果内容图片大于等于3 多图 type 3
     * 3. 如果内容没有图片 无图 type 0
     * <p>
     * 第二个功能: 保存封面图片与素材的关系
     *
     * @param dto
     * @param wmNews
     * @param contentImages
     */
    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> contentImages) {
        List<String> images = dto.getImages();

        // 如果当前封面类型为自动, 则设置封面类型数据
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            // 多图
            if (contentImages.size() >= 3) {
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = contentImages.stream().limit(3).collect(Collectors.toList());
            } else if (contentImages.size() >= 1) {
                // 单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = contentImages.stream().limit(1).collect(Collectors.toList());
            } else {
                // 无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            // 修改文章
            if (images != null && images.size() > 0) {
                wmNews.setImages(StringUtils.join(images, ","));
            }
            updateById(wmNews);
        }

        // 保存关系
        if (images != null && images.size() > 0) {
            saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
        }
    }

    /**
     * 保存文章内容图片与素材的关系
     *
     * @param contentImages
     * @param newsId
     */
    private void saveRelativeInfoForContent(List<String> contentImages, Integer newsId) {
        saveRelativeInfo(contentImages, newsId, WemediaConstants.WM_CONTENT_REFERENCE);
    }

    /**
     * 保存文章图片与素材的关系
     *
     * @param contentImages
     * @param newsId
     * @param type
     */
    private void saveRelativeInfo(List<String> contentImages, Integer newsId, Short type) {
        if (contentImages != null && !contentImages.isEmpty()) {
            List<WmMaterial> wmMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, contentImages));
            // 判断素材是否有效
            if (wmMaterials == null || wmMaterials.size() == 0 || contentImages.size() != wmMaterials.size()) {
                // 手动抛出异常 (1. 提示用户素材失效了 2. 进行素材回滚)
                throw new CustomException(AppHttpCodeEnum.MATERIAL_REFERENCE_FAIL);
            }
            List<Integer> materialIds = wmMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

            // 批量保存
            wmNewsMaterialMapper.saveRelations(materialIds, newsId, type);
        }
    }

    /**
     * 提取文章中的图片信息
     *
     * @param content
     * @return
     */
    private List<String> ectractUrlInfo(String content) {
        List<String> materials = new ArrayList<>();
        List<Map> contents = JSON.parseArray(content, Map.class);
        for (Map map : contents) {
            if (map.get("type").equals("image")) {
                String imgUrl = (String) map.get("value");
                materials.add(imgUrl);
            }
        }
        return materials;
    }

    public void saveOrUpdateWmNews(WmNews wmNews) {
        // 补全属性
        wmNews.setCreatedTime(new Date());
        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1); // 默认上架

        if (wmNews.getId() == null) {
            // 保存
            save(wmNews);
        } else {
            // 修改
            // 删除文章图片与素材的关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            updateById(wmNews);
        }
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        // 1. 检查参数
        if (dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2. 查询文章
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在!");
        }

        // 3. 判断是否发布
        if (!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前文章不是发布状态, 不能上下架!");
        }

        // 4. 修改文章的 enable 字段
        if (dto.getEnable() != null && dto.getEnable() > -1 && dto.getEnable() < 2) {
            update(Wrappers.lambdaUpdate(WmNews.class).set(WmNews::getEnable, dto.getEnable()).eq(WmNews::getId, wmNews.getId()));

            // 5. 发送消息通知
            if (wmNews.getArticleId() != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("articleId", wmNews.getArticleId());
                map.put("enable", dto.getEnable());
                kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC, JSON.toJSONString(map));
            }
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult listVo(WmNewsPageReqDto dto) {
        //1.参数检查
        dto.checkParam();

        //2.分页查询
        List<WmNewsVo> wmNewsVoList = baseMapper.findList(dto);
        int count = baseMapper.findListCount(dto);

        //3.结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), count);
        responseResult.setData(wmNewsVoList);
        return responseResult;
    }

    @Override
    public ResponseResult findOneVo(Long id) {
        // 1. 检查参数
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "缺少id参数!");
        }

        // 2. 设置参数
        WmNewsPageReqDto dto = new WmNewsPageReqDto();
        dto.setId(id);

        // 3. 查询数据
        List<WmNewsVo> list = baseMapper.findList(dto);
        if (list.isEmpty()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        return ResponseResult.okResult(list.get(0));
    }

    @Override
    public ResponseResult audit(WmNewsPageReqDto wmNewsDto) {
        // 1. 参数检查
        if (wmNewsDto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "id不能为空!");
        }
        if (wmNewsDto.getStatus() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "status不能为空!");
        }

        // 2. 查询数据并更新
        WmNews wmNews = baseMapper.selectById(wmNewsDto.getId());
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "id无效!");
        }

        // 3. 更新
        wmNews.setReason(wmNewsDto.getMsg());
        wmNews.setStatus(wmNews.getStatus());
        baseMapper.updateById(wmNews);

        // 4. 审核通过, 创建自媒体文章
        if (wmNewsDto.getStatus().equals((short) 8)) {
            // 4.1 将消息发布到任务中
            wmNewsTaskService.addNewsToTask(wmNews.getId(), wmNews.getPublishTime());
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS.getCode(), AppHttpCodeEnum.SUCCESS.getErrorMessage());
    }
}
