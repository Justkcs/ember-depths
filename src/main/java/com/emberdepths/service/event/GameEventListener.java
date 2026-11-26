package com.emberdepths.service.event;

@FunctionalInterface
public interface GameEventListener {

    void onEvent(GameEvent event);
}
