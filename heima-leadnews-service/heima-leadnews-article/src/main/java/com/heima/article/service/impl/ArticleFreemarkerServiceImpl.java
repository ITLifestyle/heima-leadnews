package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.search.vos.SearchArticleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;

    @Override
    @Async
    public void buildArticleToMinIo(ApArticle apArticle, String content) throws IOException, TemplateException {
        // 1. 获取文章内容
        if (StringUtils.isBlank(content)) {
            System.err.println("为查询到数据!");
            return;
        }

        // 2. 文章内容通过 freemarker 生成 html 文件
        Template template = configuration.getTemplate("article.ftl");
        // 数据模型
        Map<String, Object> contentDataForModel = new HashMap<>();
        contentDataForModel.put("content", JSONArray.parseArray(content));
        StringWriter out = new StringWriter();
        template.process(contentDataForModel, out);

        // 3. 把 html 上传到 minio 中
        InputStream in = new ByteArrayInputStream(out.toString().getBytes());
        String path = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", in);

        // 4. 修改 ap_article 表, 保存 static_url 字段
        apArticleService.update(Wrappers.<ApArticle>lambdaUpdate()
                .eq(ApArticle::getId, apArticle.getId())
                .set(ApArticle::getStaticUrl, path));

        // 发送消息, 创建 es 索引
        createArticleESIndex(apArticle, content, path);
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private void createArticleESIndex(ApArticle apArticle, String content, String path) {
        SearchArticleVo searchArticleVo = new SearchArticleVo();
        BeanUtils.copyProperties(apArticle, searchArticleVo);
        searchArticleVo.setContent(content);
        searchArticleVo.setStaticUrl(path);

        kafkaTemplate.send(ArticleConstants.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(searchArticleVo));
    }
}
