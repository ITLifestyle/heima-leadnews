package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.vos.WmNewsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface WmNewsMapper  extends BaseMapper<WmNews> {
    List<WmNewsVo> findList(WmNewsPageReqDto dto);

    int findListCount(WmNewsPageReqDto dto);
}
