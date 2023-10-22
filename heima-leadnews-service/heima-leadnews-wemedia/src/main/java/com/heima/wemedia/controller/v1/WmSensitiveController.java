package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.SensitiveDto;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sensitive")
public class WmSensitiveController {

    @Autowired
    private WmSensitiveService wmSensitiveService;

    @DeleteMapping("/del/{id}")
    public ResponseResult del(@PathVariable("id") Integer id) {
        return wmSensitiveService.delete(id);
    }

    @PostMapping("/list")
    public ResponseResult del(@RequestBody SensitiveDto sensitiveDto) {
        return wmSensitiveService.list(sensitiveDto);
    }

    @PostMapping("/save")
    public ResponseResult del(@RequestBody WmSensitive wmSensitive) {
        return wmSensitiveService.insert(wmSensitive);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody WmSensitive wmSensitive) {
        return wmSensitiveService.update(wmSensitive);
    }
}
