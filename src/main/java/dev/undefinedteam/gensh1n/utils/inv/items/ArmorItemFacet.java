package dev.undefinedteam.gensh1n.utils.inv.items;

import dev.undefinedteam.gensh1n.utils.inv.ItemCategory;
import dev.undefinedteam.gensh1n.utils.inv.ItemType;
import dev.undefinedteam.gensh1n.utils.inv.armor.ArmorComparator;
import dev.undefinedteam.gensh1n.utils.inv.armor.ArmorPiece;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;

import java.util.List;

public class ArmorItemFacet extends ItemFacet {
    private final List<ItemSlot> fullArmorKit;
    private final ArmorComparator armorComparator;
    private final ArmorPiece armorPiece;

    public ArmorItemFacet(ItemSlot itemSlot, List<ItemSlot> fullArmorKit, ArmorComparator armorComparator) {
        super(itemSlot);
        this.fullArmorKit = fullArmorKit;
        this.armorComparator = armorComparator;
        this.armorPiece = new ArmorPiece(itemSlot);
    }

    @Override
    public ItemCategory getCategory() {
        return new ItemCategory(ItemType.ARMOR, armorPiece.getEntitySlotId());
    }

    @Override
    public boolean shouldKeep() {
        // Sometimes there are situations where armor pieces are not the best ones with the current armor, but become
        // the best ones as soon as we upgrade one of the other armor pieces. In those cases we don't want to miss out
        // on this armor piece in the future thus we keep it.
        return this.fullArmorKit.contains(this.itemSlot);
    }

    @Override
    public int compareTo(ItemFacet other) {
        return this.armorComparator.compare(this.armorPiece, ((ArmorItemFacet) other).armorPiece);
    }
}
