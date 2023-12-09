package com.heima.apis.wemedia.fallback;

import com.heima.apis.article.IArticleClient;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmUser;
import org.springframework.stereotype.Component;

@Component
public class IWemediaClientFallback implements IWemediaClient {
    @Override
    public WmUser findWmUserByName(String username) {
        return null;
    }

    @Override
    public ResponseResult save(WmUser wmUser) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "保存错误!");
    }

    @Override
    public ResponseResult getChannels() {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "查询错误!");
    }
}
