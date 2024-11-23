package dev.undefinedteam.gensh1n.events.player;

import dev.undefinedteam.gensh1n.rotate.URotation;

public class RotationApplyEvent {
    public URotation rotation;

    public RotationApplyEvent(URotation rotation) {
        this.rotation = rotation;
    }
}
