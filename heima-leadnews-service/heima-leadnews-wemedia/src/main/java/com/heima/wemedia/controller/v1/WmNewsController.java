package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {
    @Autowired
    WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto dto){
        return wmNewsService.findList(dto);
    }

    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto) {
        return wmNewsService.submitNews(dto);
    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto) {
        return wmNewsService.downOrUp(dto);
    }

    @PostMapping("/list_vo")
    public ResponseResult listVo(@RequestBody WmNewsPageReqDto dto) {
        return wmNewsService.listVo(dto);
    }

    @GetMapping("/one_vo/{id}")
    public ResponseResult oneVo(@PathVariable("id") Long id) {
        return wmNewsService.findOneVo(id);
    }

    @PostMapping("/auth_fail")
    public ResponseResult auditFail(@RequestBody WmNewsPageReqDto wmNewsDto) {
        return wmNewsService.audit(wmNewsDto);
    }

    @PostMapping ("/auth_pass")
    public ResponseResult auditPass(@RequestBody WmNewsPageReqDto wmNewsDto) {
        return wmNewsService.audit(wmNewsDto);
    }
}
