package com.heima.apis.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("leadnews-wemedia")
public interface IWemediaClient {
    /**
     * 通过名称查询用户名
     *
     * @param name
     * @return
     */
    @GetMapping("/api/v1/user/findByName/{name}")
    WmUser findWmUserByName(@PathVariable("name") String name);

    /**
     * 通过名称查询用户名
     *
     * @param wmUser
     * @return
     */
    @PostMapping("/api/v1/user/save")
    ResponseResult save(@RequestBody WmUser wmUser);
}
