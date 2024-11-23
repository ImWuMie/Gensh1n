package dev.undefinedteam.gensh1n.events.game;

import dev.undefinedteam.gensh1n.events.Cancellable;

public class SendMessageEvent extends Cancellable {

    public String message;

    public static SendMessageEvent get(String message) {
        SendMessageEvent event = new SendMessageEvent();
        event.message = message;
        return event;
    }
}


