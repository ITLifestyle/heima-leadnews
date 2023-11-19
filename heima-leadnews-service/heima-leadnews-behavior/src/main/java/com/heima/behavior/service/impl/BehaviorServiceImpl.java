package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.heima.apis.article.IArticleClient;
import com.heima.behavior.service.BehaviorService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.behavior.dtos.CountBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.ApThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class BehaviorServiceImpl implements BehaviorService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private IArticleClient articleClient;

    @Override
    public ResponseResult like(LikesBehaviorDto likeDto) {
        // 1. 检测参数
        if (likeDto == null || likeDto.getArticleId() == null || likeDto.getOperation() == null || likeDto.getType() == null
                || !checkLikeParam(likeDto)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "参数无效!");
        }

        // 2. 查询文章, 这个id好像和数据库的不一样, 去掉该校验
        /*ApArticle article = articleClient.findArticle(likeDto.getArticleId());
        if (article == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "查询文章失败!");
        }*/

        // 3. 获取登陆人
        ApUser user = ApThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 4. 存数据
        String redisKey = BehaviorConstants.REDIS_ARTICLE_LIKE_KEY + ":" + likeDto.getArticleId();
        if (likeDto.getOperation() == 0) {
            // 校验是否已经点赞
            Object obj = cacheService.hGet(redisKey, user.getId().toString());
            if (obj != null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "已点赞!");
            }
            // 保存点赞信息
            log.info("点赞: {},{},{}", redisKey, user.getId().toString(), JSON.toJSONString(likeDto));
            cacheService.hPut(redisKey, user.getId().toString(), JSON.toJSONString(likeDto));
        } else if (likeDto.getOperation() == 1) {
            // 删除点赞
            log.info("删除点赞: {},{},{}", redisKey, user.getId().toString(), JSON.toJSONString(likeDto));
            cacheService.hDelete(redisKey, user.getId().toString());
        }

        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult readCount(CountBehaviorDto countDto) {
        // 1. 检查参数
        if (countDto == null || countDto.getArticleId() == null || countDto.getCount() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        // 2. 查询文章, 这个id好像和数据库的不一样, 去掉该校验
        /*ApArticle article = articleClient.findArticle(countDto.getArticleId());
        if (article == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "查询文章失败!");
        }*/

        // 3. 获取登陆人
        ApUser user = ApThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 4. 计数
        // 查询现有的计数信息并累加
        String redisKey = BehaviorConstants.REDIS_ARTICLE_COUNT_KEY + ":" + countDto.getArticleId();
        Object obj = cacheService.hGet(redisKey, user.getId().toString());
        if (obj != null && StringUtils.isNotBlank(obj.toString())) {
            CountBehaviorDto historyCountDto = JSONObject.parseObject(obj.toString(), CountBehaviorDto.class);
            countDto.setCount(countDto.getCount() + historyCountDto.getCount());
        }
        // 设置计数信息
        log.info("计数: {}, {}, {}", countDto.getArticleId(), user.getId(), countDto.getCount());
        cacheService.hPut(redisKey, user.getId().toString(), JSON.toJSONString(countDto));
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult unlike(UnLikesBehaviorDto unLikeDto) {
        // 1. 检查参数
        if (unLikeDto == null || unLikeDto.getArticleId() == null || unLikeDto.getType() == null
            || !checkUnLikeParam(unLikeDto)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        // 2. 查询文章, 这个id好像和数据库的不一样, 去掉该校验
        /*ApArticle article = articleClient.findArticle(unLikeDto.getArticleId());
        if (article == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "查询文章失败!");
        }*/

        // 3. 获取登陆人
        ApUser user = ApThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 4. 操作
        String redisKey = BehaviorConstants.REDIS_ARTICLE_UNLIKE_KEY + ":" + unLikeDto.getArticleId();
        if (unLikeDto.getType() == 0) {
            // 查询
            Object obj = cacheService.hGet(redisKey, user.getId().toString());
            if (obj != null && StringUtils.isBlank(obj.toString())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "已经不喜欢了!");
            }
            // 保存
            log.error("不喜欢操作 : {}, {}, {}", unLikeDto.getArticleId(), user.getId(), unLikeDto.getType());
            cacheService.hPut(redisKey, user.getId().toString(), JSON.toJSONString(unLikeDto));
        } else if (unLikeDto.getType() == 1) {
            // 删除
            log.error("取消不喜欢操作 : {}, {}, {}", unLikeDto.getArticleId(), user.getId(), unLikeDto.getType());
            cacheService.hDelete(redisKey, user.getId().toString());
        }
        return ResponseResult.okResult();
    }

    private boolean checkLikeParam(LikesBehaviorDto likeDto) {
        if (likeDto.getType() == null
                || ! (likeDto.getType() == 0 || likeDto.getType() == 1 || likeDto.getType() == 2)) {
            return false;
        }

        if (likeDto.getOperation() == null
                || !(likeDto.getOperation() == 0 || likeDto.getOperation() == 1)) {
            return false;
        }

        return true;
    }

    private boolean checkUnLikeParam(UnLikesBehaviorDto unLikesDto) {
        if (unLikesDto.getType() == null
                || !(unLikesDto.getType() == 0 || unLikesDto.getType() == 1)) {
            return false;
        }

        return true;
    }
}
