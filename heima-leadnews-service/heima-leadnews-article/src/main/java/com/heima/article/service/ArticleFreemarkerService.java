package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;
import freemarker.template.TemplateException;

import java.io.IOException;

public interface ArticleFreemarkerService {
    /**
     * 生成静态文件到minIO中
     * @param apArticle
     * @param content
     */
    void buildArticleToMinIo(ApArticle apArticle, String content) throws IOException, TemplateException;
}
