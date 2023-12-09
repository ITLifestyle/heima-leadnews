package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.constants.UserConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.vos.HotArticleVo;
import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.ApThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    private final static short MAX_PAGE_SIZE = 50;

    @Override
    public ResponseResult load(ArticleHomeDto dto, Short type) {
        // 参数校验
        // 分页条数校验
        Integer size = dto.getSize();
        if (size == null || size == 0) {
            size = 10;
        }
        // 分页的值不超过50
        size = Math.min(MAX_PAGE_SIZE, size);

        // 校验 type 参数
        if (!type.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !type.equals(ArticleConstants.LOADTYPE_LOAD_NEW)) {
            type = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        // 频道参数校验
        if (StringUtils.isBlank(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        // 时间校验
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }
        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }

        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, type);
        return ResponseResult.okResult(apArticles);
    }

    @Override
    public ResponseResult load2(ArticleHomeDto dto, Short type, boolean firstPage) {
        if (firstPage) {
            String jsonStr = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            if (StringUtils.isNotBlank(jsonStr)) {
                List<HotArticleVo> hotArticleVos = JSON.parseArray(jsonStr, HotArticleVo.class);
                ResponseResult responseResult = ResponseResult.okResult(hotArticleVos);
                return responseResult;
            }
        }
        return load(dto, type);
    }

    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;

    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/

        // 1. 检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);
        // 2. 判断是否存在id
        if (dto.getId() == null) {
            // 2.1 不存在id 保存 文章/文章配置/文章内容
            save(apArticle);
            // 保存配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);

            // 保存文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);
        } else {
            // 2.2 存在id 修改 文章/文章内容
            // 修改文章
            updateById(apArticle);
            // 修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        // 异步调用生成静态文件
        try {
            articleFreemarkerService.buildArticleToMinIo(apArticle, dto.getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. 结果返回, 返回文章的id
        return ResponseResult.okResult(apArticle.getId());
    }

    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto) {
        // 1. 检查参数
        if (dto == null || dto.getArticleId() == null || dto.getAuthorId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        boolean isfollow =false, islike = false, isunlike = false, iscollection = false;

        ApUser user = ApThreadLocalUtil.getUser();
        if (user != null) {
            // 获取点赞行为数据
            Object likeBehavior = cacheService.hGet(BehaviorConstants.REDIS_ARTICLE_LIKE_KEY + ":" + dto.getArticleId(), user.getId().toString());
            if (likeBehavior != null && StringUtils.isNotBlank(likeBehavior.toString())) {
                islike = true;
            }

            // 获取点赞行为数据
            Object unLikeBehavior = cacheService.hGet(BehaviorConstants.REDIS_ARTICLE_UNLIKE_KEY + ":" + dto.getArticleId(), user.getId().toString());
            if (unLikeBehavior != null && StringUtils.isNotBlank(unLikeBehavior.toString())) {
                isunlike = true;
            }

            // 获取行为行为数据
            Object collectionBehavior = cacheService.hGet(BehaviorConstants.REDIS_ARTICLE_COLLECTION_KEY + ":" + dto.getArticleId(), user.getId().toString());
            if (collectionBehavior != null && StringUtils.isNotBlank(collectionBehavior.toString())) {
                iscollection = true;
            }

            // 获取行为行为数据
            Double score = cacheService.zScore(UserConstants.REDIS_USER_FOLLOW_KEY + ":" + user.getId(), dto.getAuthorId().toString());
            if (score != null) {
                isfollow = true;
            }
        }

        // 封装结果并返回
        Map<String, Boolean> result = new HashMap<>();
        result.put("islike", islike);
        result.put("isunlike", isunlike);
        result.put("iscollection", iscollection);
        result.put("isfollow", isfollow);

        return ResponseResult.okResult(result);
    }

    @Override
    public ResponseResult collection(CollectionBehaviorDto collectionDto) {
        // 1. 检查参数
        if (collectionDto == null || collectionDto.getEntryId() == null || collectionDto.getOperation() == null
                || collectionDto.getType() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        // 2. 查询文章, 这个id好像和数据库的不一样, 去掉该校验
        /*ApArticle article = articleClient.findArticle(collectionDto.getEntryId());
        if (article == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "查询文章失败!");
        }*/

        // 3. 获取用户信息
        ApUser user = ApThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 4. 插入redis
        String redisKey = BehaviorConstants.REDIS_ARTICLE_COLLECTION_KEY + ":" + collectionDto.getEntryId();
        if (collectionDto.getOperation() == 0) {
            // 查询现有信息
            Object obj = cacheService.hGet(redisKey, user.getId().toString());
            if (obj != null && StringUtils.isBlank(obj.toString())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "不要重复收藏!");
            }
            // 保存信息
            log.info("保存收藏信息: {}, {}, {}", collectionDto.getEntryId(), user.getId(), collectionDto.getType());
            cacheService.hPut(redisKey, user.getId().toString(), JSON.toJSONString(collectionDto));
        } else if (collectionDto.getOperation() == 1) {
            // 删除收藏信息
            log.info("保存收藏信息: {}, {}, {}", collectionDto.getEntryId(), user.getId(), collectionDto.getType());
            cacheService.hDelete(redisKey, user.getId().toString());
        }

        return ResponseResult.okResult();
    }
}
