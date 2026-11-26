package com.emberdepths.service.event;

import java.util.Objects;

public record GameEvent(EventType type, String message) {

    public GameEvent {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(message, "message");
    }
}
