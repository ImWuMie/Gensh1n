package dev.undefinedteam.gensh1n.events.client;

public class NotificationEvent {
    public String title,message;
    public Severity severity;

    public NotificationEvent(String title, String message, Severity severity) {
        this.title = title;
        this.message = message;
        this.severity = severity;
    }

    public enum Severity {
        INFO, SUCCESS, ERROR, ENABLED, DISABLED
    }
}
