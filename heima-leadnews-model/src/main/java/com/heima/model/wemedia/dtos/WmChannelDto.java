package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class WmChannelDto extends PageRequestDto {
    /**
     * 频道名称
     */
    private String name;
}
