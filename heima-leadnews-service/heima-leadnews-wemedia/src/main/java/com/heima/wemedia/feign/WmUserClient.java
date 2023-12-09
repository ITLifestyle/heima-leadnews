package com.heima.wemedia.feign;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.service.WmChannelService;
import com.heima.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class WmUserClient implements IWemediaClient {
    @Autowired
    private WmUserService wmUserService;

    @GetMapping("/api/v1/user/findByName/{name}")
    public WmUser findWmUserByName(@PathVariable("name") String username) {
        return wmUserService.findByUsername(username);
    }

    @PostMapping("/api/v1/user/save")
    public ResponseResult save(WmUser wmUser) {
        return wmUserService.insert(wmUser);
    }

    @Autowired
    private WmChannelService wmChannelService;

    @Override
    @GetMapping("/api/v1/channel/list")
    public ResponseResult getChannels() {
        return wmChannelService.findAll();
    }
}
