package com.heima.xxljob.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelloJob {
    @Value("${server.port}")
    private String port;

    @XxlJob("demo-JobHandler")
    public void helloJob() {
        System.out.println("任务执行了...." + port);
    }

    /**
     * 分片案例
     */
    @XxlJob("sharding-JobHandler")
    public void shardingDemo() {
        // 片头参数
        // 当前分片
        int shardIndex = XxlJobHelper.getShardIndex();
        // 总分片
        int shardTotal = XxlJobHelper.getShardTotal();

        // 业务逻辑
        List<Integer> list = getList();
        for (Integer integer : list) {
            if (integer % shardTotal == shardIndex) {
                System.out.println("当前第" + shardIndex + "分片执行了, 任务项目为: " + integer);
            }
        }
    }

    public List<Integer> getList() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        return list;
    }
}
