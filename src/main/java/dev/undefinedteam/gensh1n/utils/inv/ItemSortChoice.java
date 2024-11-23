package dev.undefinedteam.gensh1n.utils.inv;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.function.Predicate;

// Assuming ItemSortChoice is an enum that represents the sort choice of an item
public enum ItemSortChoice {
    SWORD("Sword", new ItemCategory(ItemType.SWORD, 0)),
    WEAPON("Weapon", new ItemCategory(ItemType.WEAPON, 0)),
    BOW("Bow", new ItemCategory(ItemType.BOW, 0)),
    CROSSBOW("Crossbow", new ItemCategory(ItemType.CROSSBOW, 0)),
    AXE("Axe", new ItemCategory(ItemType.TOOL, 0)),
    PICKAXE("Pickaxe", new ItemCategory(ItemType.TOOL, 1)),
    ROD("Rod", new ItemCategory(ItemType.ROD, 0)),
    SHIELD("Shield", new ItemCategory(ItemType.SHIELD, 0)),
    WATER("Water", new ItemCategory(ItemType.BUCKET, 0)),
    LAVA("Lava", new ItemCategory(ItemType.BUCKET, 1)),
    MILK("Milk", new ItemCategory(ItemType.BUCKET, 2)),
    PEARL("Pearl", new ItemCategory(ItemType.PEARL, 0), itemStack -> itemStack.getItem() == Items.ENDER_PEARL),
    GAPPLE("Gapple", new ItemCategory(ItemType.GAPPLE, 0), itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE || itemStack.getItem() == Items.ENCHANTED_GOLDEN_APPLE),
    FOOD("Food", new ItemCategory(ItemType.FOOD, 0), itemStack -> itemStack.getFoodComponent() != null),
    BLOCK("Block", new ItemCategory(ItemType.BLOCK, 0), itemStack -> itemStack.getItem() instanceof BlockItem),
    THROWABLES("Throwables", new ItemCategory(ItemType.THROWABLE, 0)),
    IGNORE("Ignore", null),
    NONE("None", null);

    private final String choiceName;
    private final ItemCategory category;
    private final Predicate<ItemStack> satisfactionCheck;

    ItemSortChoice(String choiceName, ItemCategory category) {
        this(choiceName, category, null);
    }

    ItemSortChoice(String choiceName, ItemCategory category, Predicate<ItemStack> satisfactionCheck) {
        this.choiceName = choiceName;
        this.category = category;
        this.satisfactionCheck = satisfactionCheck;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public Predicate<ItemStack> getSatisfactionCheck() {
        return satisfactionCheck;
    }

    @Override
    public String toString() {
        return choiceName;
    }
}
