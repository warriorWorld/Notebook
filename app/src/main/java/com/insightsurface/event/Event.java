package com.insightsurface.event;

import com.insightsurface.lib.eventbus.EventBusEvent;

public class Event extends EventBusEvent {
    public static final int LOGOUT_EVENT = 18;

    public Event(int eventType) {
        super(eventType);
    }

    public Event(String msg) {
        super(msg);
    }

    public Event(String msg, int eventType) {
        super(msg, eventType);
    }

    public Event(String msg, int intMsg, int eventType) {
        super(msg, intMsg, eventType);
    }

}
