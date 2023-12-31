package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.ApAuthDto;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.model.user.pojos.ApUser;

public interface ApUserService extends IService<ApUser> {
    /**
     *
     * @param loginDto
     * @return
     */
    ResponseResult login(LoginDto loginDto);

    /**
     * 关注
     *
     * @param relDto
     * @return
     */
    ResponseResult follow(UserRelationDto relDto);
}
