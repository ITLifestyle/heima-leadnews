package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.HistorySearchDto;

public interface ApUserSearchService {
    /**
     * 保存用户搜索历史记录
     *
     * @param keyword
     * @param userId
     */
    void insert(String keyword, Integer userId);

    /**
     * 查询用户搜索历史
     *
     * @return
     */
    ResponseResult findUserSearch();

    /**
     * 删除记录
     *
     * @param dto
     * @return
     */
    ResponseResult delUserSearch(HistorySearchDto dto);
}
