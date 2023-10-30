package com.heima.behavior.controller.v1;

import com.heima.model.behavior.dtos.CollectionBehaviorDto;
import com.heima.model.behavior.dtos.CountBehaviorDto;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class BehaviorController {
    @PostMapping("/likes_behavior")
    public ResponseResult likesBehavior(@RequestBody LikesBehaviorDto likesBehaviorDto) {
        return ResponseResult.okResult();
    }

    @PostMapping("/read_behavior")
    public ResponseResult readBehavior(@RequestBody CountBehaviorDto countBehaviorDto) {
        return ResponseResult.okResult();
    }

    @PostMapping("/un_likes_behavior")
    public ResponseResult unLikesBehavior(@RequestBody UnLikesBehaviorDto unLikesBehaviorDto) {
        return ResponseResult.okResult();
    }

    @PostMapping("/collection_behavior")
    public ResponseResult collectionBehavior(@RequestBody CollectionBehaviorDto collectionBehaviorDto) {
        return ResponseResult.okResult();
    }
}
