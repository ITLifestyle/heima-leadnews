package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class SensitiveDto extends PageRequestDto {
    /**
     * 敏感吃名称
     */
    private String name;
}
