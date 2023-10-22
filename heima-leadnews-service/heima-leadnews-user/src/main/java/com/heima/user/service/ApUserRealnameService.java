package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.ApAuthDto;
import com.heima.model.user.pojos.ApUserRealname;

public interface ApUserRealnameService extends IService<ApUserRealname> {
    /**
     * 查询
     *
     * @param apAuthDto
     * @return
     */
    ResponseResult list(ApAuthDto apAuthDto);

    ResponseResult changeStatus(ApAuthDto dto, Short status);
}
