package dev.undefinedteam.gensh1n.events.player;

import dev.undefinedteam.gensh1n.events.Cancellable;
import net.minecraft.screen.slot.SlotActionType;

/**
 * @Author KuChaZi
 * @Date 2024/11/8 16:03
 * @ClassName: ClickSlotEvent
 */
public class ClickSlotEvent extends Cancellable {
    private final SlotActionType slotActionType;
    private final int slot, button, id;

    public ClickSlotEvent(SlotActionType slotActionType, int slot, int button, int id) {
        this.slot = slot;
        this.button = button;
        this.id = id;
        this.slotActionType = slotActionType;
    }

    public SlotActionType getSlotActionType() {
        return slotActionType;
    }

    public int getSlot() {
        return slot;
    }

    public int getButton() {
        return button;
    }

    public int getId() {
        return id;
    }
}
