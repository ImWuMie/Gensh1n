package dev.undefinedteam.gensh1n.utils.inv.items;

import dev.undefinedteam.gensh1n.utils.inv.ItemCategory;
import dev.undefinedteam.gensh1n.utils.inv.ItemType;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;

/**
 * Specialization of weapon type. Used in order to allow the user to specify that they want a sword and not an axe
 * or something.
 */
public class SwordItemFacet extends WeaponItemFacet {
    public SwordItemFacet(ItemSlot itemSlot) {
        super(itemSlot);
    }

    @Override
    public ItemCategory getCategory() {
        return new ItemCategory(ItemType.SWORD, 0);
    }
}
