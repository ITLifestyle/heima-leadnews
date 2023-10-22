package com.heima.model.user.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class ApAuthDto extends PageRequestDto {
    /**
     * id
     */
    private Integer id;

    /**
     * 消息
     */
    private String msg;

    /**
     * 状态
     */
    private Integer status;
}
