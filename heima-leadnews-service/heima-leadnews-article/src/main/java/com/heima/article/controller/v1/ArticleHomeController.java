package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article")
@Api(value = "获取文章信息", tags = "获取文章信息")
public class ArticleHomeController {
    @Autowired
    private ApArticleService apArticleService;

    @RequestMapping("/load")
    @ApiOperation("加载新闻信息")
    public ResponseResult load(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.load2(articleHomeDto, ArticleConstants.LOADTYPE_LOAD_MORE, true);
    }

    @RequestMapping("/loadmore")
    @ApiOperation("加载更多信息")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.load(articleHomeDto, ArticleConstants.LOADTYPE_LOAD_MORE);
    }

    @RequestMapping("/loadnew")
    @ApiOperation("加载最新信息")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto articleHomeDto) {
        return apArticleService.load(articleHomeDto, ArticleConstants.LOADTYPE_LOAD_NEW);
    }

    @PostMapping("/load_article_behavior")
    public ResponseResult loadArticleBehavior(@RequestBody ArticleInfoDto articleInfoDto) {
        return apArticleService.loadArticleBehavior(articleInfoDto);
    }
}
