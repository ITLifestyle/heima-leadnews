package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import org.springframework.web.bind.annotation.RequestBody;

public interface WmNewsService extends IService<WmNews> {


    ResponseResult findList(WmNewsPageReqDto dto);

    ResponseResult submitNews(WmNewsDto dto);

    ResponseResult downOrUp(WmNewsDto dto);

    ResponseResult listVo(WmNewsPageReqDto dto);

    ResponseResult findOneVo(Long id);

    /**
     * 审批
     *
     * @param wmNewsDto
     * @return
     */
    ResponseResult audit(WmNewsPageReqDto wmNewsDto);
}
