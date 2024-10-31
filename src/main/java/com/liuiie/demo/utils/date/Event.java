package com.liuiie.demo.utils.date;

import java.util.Date;

/**
 * Event
 *
 * @author Liuiie
 * @since 2024/10/31 9:34
 */
public class Event {
    private String name;
    private Date startTime;
    private Date endTime;

    public Event(String name, Date startTime, Date endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}
