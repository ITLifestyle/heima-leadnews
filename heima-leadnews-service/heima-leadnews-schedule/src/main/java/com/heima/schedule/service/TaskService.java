package com.heima.schedule.service;

import com.heima.model.schedule.dtos.Task;

public interface TaskService {
    /**
     * 添加延迟任务
     *
     * @param task
     * @return
     */
    long addTask(Task task);

    /**
     * 取消定时任务
     *
     * @param taskId
     * @return
     */
    boolean cancelTask(long taskId);

    /**
     * 拉取任务
     *
     * @param type
     * @param priority
     * @return
     */
    Task poll(int type, int priority);
}
