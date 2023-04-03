package com.liuiie.demo.utils.quartz;

import org.springframework.stereotype.Service;

/**
 * MessagePushService
 *
 * @author Liuiie
 * @since 2022/4/28 23:24
 */
@Service("mpm")
public class MessagePushService {
    public void pushMessage(String message) {
        System.out.println(message);
    }
}
