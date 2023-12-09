package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {
    /**
     * 加载文档列表
     *
     * @param dto
     * @type    1. 加载更多 2. 加载最新
     * @return
     */
    List<ApArticle> loadArticleList(ArticleHomeDto dto, Short type);

    /**
     * 通过日期查询之后的所有数据
     *
     * @param date
     * @return
     */
    List<ApArticle> findAfterArticleByDate(@Param("date") Date date);
}
