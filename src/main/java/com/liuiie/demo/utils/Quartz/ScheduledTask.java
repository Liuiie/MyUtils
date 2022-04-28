package com.liuiie.demo.utils.Quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * ScheduledTask
 *
 * @author Liuiie
 * @since 2022/4/28 23:22
 */
public class ScheduledTask implements Job {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("===================任务开始执行===================:" + new Date());
        //获取bean对象；
        /**
         * @Service("mpm")
            public class MessagePushServiceImpl implements MessagePushService {
         */
        MessagePushService messagePushService = SpringContextUtil.getBean("mpm", MessagePushService.class);
        //...具体操作
        messagePushService.pushMessage("定时任务执行了");

        logger.info("===================任务执行完成===================："+ new Date());
    }
}
