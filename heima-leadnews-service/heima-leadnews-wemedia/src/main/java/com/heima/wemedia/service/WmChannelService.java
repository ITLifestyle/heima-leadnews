package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;

public interface WmChannelService extends IService<WmChannel> {
    /**
     * 查询所有信息
     *
     * @return
     */
    ResponseResult findAll();

    /**
     * 删除数据
     *
     * @param id
     * @return
     */
    ResponseResult delete(Integer id);

    /**
     * 查询
     *
     * @param dto
     * @return
     */
    ResponseResult list(WmChannelDto dto);

    /**
     * 插入数据
     *
     * @param wmChannel
     * @return
     */
    ResponseResult insert(WmChannel wmChannel);

    ResponseResult update(WmChannel wmChannel);
}