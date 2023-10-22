package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.ApAuthDto;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    @Override
    public ResponseResult login(LoginDto dto) {
        // 1. 正常登录
        if (StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword())) {
            // 查询用户
            ApUser dbUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
            if (dbUser == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST, "用户查询为空!");
            }

            // 对比密码
            String salt = dbUser.getSalt();
            String password = dto.getPassword();
            String pass = DigestUtils.md5DigestAsHex((salt + password).getBytes());
            if (!pass.equals(dbUser.getPassword())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "用户名或密码错误!");
            }

            // 数据发返回, jwt
            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            // 设置返回信息
            Map<String, Object> rtnData = new HashMap<>();
            rtnData.put("user", dbUser);
            // 脱敏
            dbUser.setSalt("");
            dbUser.setPassword("");
            // 返回token
            rtnData.put("token", token);
            return ResponseResult.okResult(rtnData);
        } else {
            // 2. 游客登录
            Map<String, Object> rtnData = new HashMap<>();
            rtnData.put("token", AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(rtnData);
        }
    }
}
