package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;

public interface ApArticleService extends IService<ApArticle> {
    /**
     * 加载文章列表
     *
     * @param dto 参数
     * @param type 1.加载更多 2. 加载最新
     * @return
     */
    ResponseResult load(ArticleHomeDto dto, Short type);

    /**
     * 加载文章列表
     *
     * @param dto 参数
     * @param type 1.加载更多 2. 加载最新
     * @param firstPage true 为首页
     * @return
     */
    ResponseResult load2(ArticleHomeDto dto, Short type, boolean firstPage);


    /**
     * 保存app端相关文章
     *
     * @param articleDto
     */
    ResponseResult saveArticle(ArticleDto articleDto);

    /**
     * 加载行为信息
     *
     * @param articleInfoDto
     * @return
     */
    ResponseResult loadArticleBehavior(ArticleInfoDto articleInfoDto);

    /**
     * 收藏文章
     *
     * @param collectionBehaviorDto
     * @return
     */
    ResponseResult collection(CollectionBehaviorDto collectionBehaviorDto);
}
