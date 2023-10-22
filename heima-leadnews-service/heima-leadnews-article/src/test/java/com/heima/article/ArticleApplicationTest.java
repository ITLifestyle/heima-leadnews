package com.heima.article;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleApplicationTest {
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;

    @Test
    public void createStaticUrlTest() throws Exception {
        // 1. 获取文章内容
        ApArticleContent apArticleContent = apArticleContentMapper
                .selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, "1383827787629252610"));
        if (apArticleContent == null || StringUtils.isBlank(apArticleContent.getContent())) {
            System.err.println("为查询到数据!");
            return;
        }

        // 2. 文章内容通过 freemarker 生成 html 文件
        Template template = configuration.getTemplate("article.ftl");
        // 数据模型
        Map<String, Object> content = new HashMap<>();
        content.put("content", JSONArray.parseArray(apArticleContent.getContent()));
        StringWriter out = new StringWriter();
        template.process(content, out);

        // 3. 把 html 上传到 minio 中
        InputStream in = new ByteArrayInputStream(out.toString().getBytes());
        String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", in);

        // 4. 修改 ap_article 表, 保存 static_url 字段
        apArticleService.update(Wrappers.<ApArticle>lambdaUpdate()
                .eq(ApArticle::getId, apArticleContent.getArticleId())
                .set(ApArticle::getStaticUrl, path));
    }
}
