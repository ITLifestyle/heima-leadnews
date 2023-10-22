package com.heima.user.controller.v1;

import com.heima.common.constants.UserConstants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.ApAuthDto;
import com.heima.user.service.ApUserRealnameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class ApUserRealnameController {
    @Autowired
    private ApUserRealnameService apUserRealnameService;

    @PostMapping("/list")
    public ResponseResult list(@RequestBody ApAuthDto apAuthDto) {
        return apUserRealnameService.list(apAuthDto);
    }

    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody ApAuthDto apAuthDto) {
        return apUserRealnameService.changeStatus(apAuthDto, UserConstants.AUTH_FAIL);
    }

    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody ApAuthDto apAuthDto) {
        return apUserRealnameService.changeStatus(apAuthDto, UserConstants.AUTH_PASS);
    }
}
