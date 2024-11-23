package dev.undefinedteam.gensh1n.events;

public interface SingleThreadEvent {
    <T extends SingleThreadEvent> T self();
}
