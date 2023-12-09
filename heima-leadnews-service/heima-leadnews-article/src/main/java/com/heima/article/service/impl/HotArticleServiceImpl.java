package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.vos.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private IWemediaClient wemediaClient;

    @Autowired
    private CacheService cacheService;

    @Override
    public void computeHotArticle() {
        // 1. 查询前5天的数据
        Date date = DateTime.now().minusDays(150).toDate();
        List<ApArticle> apArticles = apArticleMapper.findAfterArticleByDate(date);

        // 2. 计算文章的分值
        List<HotArticleVo> hotArticleVos = computeHotArticle(apArticles);

        // 3. 将30个热点文章数据设置到redis中
        cacheToRedis(hotArticleVos);
    }

    private void cacheToRedis(List<HotArticleVo> hotArticleVos) {
        // 查询所有的频道
        ResponseResult responseResult = wemediaClient.getChannels();
        if (responseResult.getCode().equals(AppHttpCodeEnum.SUCCESS.getCode())) {
            String channelJson = JSON.toJSONString(responseResult.getData());
            List<WmChannel> wmChannels = JSON.parseArray(channelJson, WmChannel.class);

            if (wmChannels == null || wmChannels.isEmpty()) {
                return;
            }

            for (WmChannel wmChannel : wmChannels) {
                List<HotArticleVo> hotArticleVoList = hotArticleVos.stream().filter(article -> article.getChannelId() != null && article.getChannelId().equals(wmChannel.getId())).collect(Collectors.toList());

                // 文章排序, 取前30条
                sortAndCache(hotArticleVoList, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + wmChannel.getId());
            }
        }

        // 设置推荐数据
        sortAndCache(hotArticleVos, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);
    }

    private void sortAndCache(List<HotArticleVo> hotArticleVos, String key) {
        List<HotArticleVo> hotArticles = hotArticleVos.stream()
                .sorted(Comparator.comparing(HotArticleVo::getScore).reversed())
                .limit(30)
                .collect(Collectors.toList());

        cacheService.set(key, JSON.toJSONString(hotArticles));
    }


    private List<HotArticleVo> computeHotArticle(List<ApArticle> apArticles) {
        if (apArticles == null || apArticles.isEmpty()) {
            return Collections.emptyList();
        }

        List<HotArticleVo> hotArticleVos = new ArrayList<>();
        for (ApArticle apArticle : apArticles) {
            HotArticleVo hotArticleVo = new HotArticleVo();
            Integer score = computeScore(apArticle);
            hotArticleVo.setScore(score);
            BeanUtils.copyProperties(apArticle, hotArticleVo);
            hotArticleVos.add(hotArticleVo);
        }
        return hotArticleVos;
    }

    private Integer computeScore(ApArticle apArticle) {
        Integer score = 0;
        if (apArticle.getLikes() != null) {
            score += apArticle.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (apArticle.getViews() != null) {
            score += apArticle.getViews();
        }
        if (apArticle.getComment() != null) {
            score += apArticle.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if (apArticle.getCollection() != null) {
            score += apArticle.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }
        return score;
    }
}
