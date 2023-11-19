package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Api(value = "收藏文章", tags = "收藏文章")
public class ArticleCollectionController {
    @Autowired
    private ApArticleService apArticleService;

    @PostMapping("/collection_behavior/")
    public ResponseResult collectionBehavior(@RequestBody CollectionBehaviorDto collectionBehaviorDto) {
        return apArticleService.collection(collectionBehaviorDto);
    }
}
