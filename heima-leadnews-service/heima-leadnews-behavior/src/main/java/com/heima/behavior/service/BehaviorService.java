package com.heima.behavior.service;

import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.behavior.dtos.CountBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface BehaviorService {
    /**
     * 用户喜欢行为
     *
     * @param likesBehaviorDto
     * @return
     */
    ResponseResult like(LikesBehaviorDto likesBehaviorDto);

    /**
     * 统计文章的阅读次数
     *
     * @param countBehaviorDto
     * @return
     */
    ResponseResult readCount(CountBehaviorDto countBehaviorDto);

    /**
     * 不喜欢操作
     *
     * @param unLikesBehaviorDto
     * @return
     */
    ResponseResult unlike(UnLikesBehaviorDto unLikesBehaviorDto);


    /**
     * 收藏操作
     *
     * @param collectionBehaviorDto
     * @return
     */
    ResponseResult collection(CollectionBehaviorDto collectionBehaviorDto);
}
