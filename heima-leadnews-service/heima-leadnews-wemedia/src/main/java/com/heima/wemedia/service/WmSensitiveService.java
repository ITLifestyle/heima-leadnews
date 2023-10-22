package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.SensitiveDto;
import com.heima.model.wemedia.pojos.WmSensitive;

public interface WmSensitiveService extends IService<WmSensitive> {
    /**
     * 删除敏感词
     *
     * @param id
     * @return
     */
    ResponseResult delete(Integer id);

    /**
     * 列表页查询
     *
     * @param sensitiveDto
     * @return
     */
    ResponseResult list(SensitiveDto sensitiveDto);

    /**
     * 添加敏感词
     *
     * @param wmSensitive
     * @return
     */
    ResponseResult insert(WmSensitive wmSensitive);

    /**
     * 更新敏感词
     *
     * @param wmSensitive
     * @return
     */
    ResponseResult update(WmSensitive wmSensitive);
}
