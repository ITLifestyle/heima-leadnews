package com.heima.model.search.dtos;

import lombok.Data;

import java.util.Date;


@Data
public class UserSearchDto {

    /**
    * 搜索关键字
    */
    String searchWords;
    /**
    * 当前页
    */
    int pageNum;
    /**
    * 分页条数
    */
    int pageSize;
    /**
    * 最小时间 (下拉的时候防止新建的数据导致重复数据的出现, 比如现在查的 1-10条数据, 下拉查询时突然新增加了一条数据, 上一页的10条现在变为了11条, 这个过程查询 11-20时, 11条其实上一次查询已经查询出来了)
    */
    Date minBehotTime;

    public int getFromIndex(){
        if(this.pageNum<1)return 0;
        if(this.pageSize<1) this.pageSize = 10;
        return this.pageSize * (pageNum-1);
    }
}