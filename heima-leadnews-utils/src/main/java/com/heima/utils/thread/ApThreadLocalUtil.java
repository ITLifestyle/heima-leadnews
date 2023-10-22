package com.heima.utils.thread;

import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;

public class ApThreadLocalUtil {
    private final static ThreadLocal<ApUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    // 存入线程
    public static void setUser(ApUser wmUser) {
        WM_USER_THREAD_LOCAL.set(wmUser);
    }

    // 从线程中获取
    public static ApUser getUser() {
        return WM_USER_THREAD_LOCAL.get();
    }

    // 清理
    public static void clear() {
        WM_USER_THREAD_LOCAL.remove();
    }
}
