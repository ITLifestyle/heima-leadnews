package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmLoginDto;
import com.heima.model.wemedia.pojos.WmUser;

public interface WmUserService extends IService<WmUser> {

    /**
     * 自媒体端登录
     * @param dto
     * @return
     */
    public ResponseResult login(WmLoginDto dto);

    /**
     * 名称
     *
     * @param username
     * @return
     */
    WmUser findByUsername(String username);

    /**
     * 插入数据
     *
     * @param wmUser
     * @return
     */
    ResponseResult insert(WmUser wmUser);
}