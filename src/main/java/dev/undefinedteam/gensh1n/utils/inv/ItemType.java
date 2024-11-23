package dev.undefinedteam.gensh1n.utils.inv;

// Assuming ItemType is an enum that represents the type of an item
public enum ItemType {
    ARMOR(true, 20),
    SWORD(true, 10, ItemFunction.WEAPON_LIKE),
    WEAPON(true, -1, ItemFunction.WEAPON_LIKE),
    BOW(true),
    CROSSBOW(true),
    ARROW(true),
    TOOL(true, 10),
    ROD(true),
    THROWABLE(false),
    SHIELD(true),
    FOOD(false),
    BUCKET(false),
    PEARL(false),
    GAPPLE(false),
    BLOCK(false),
    NONE(false);

    private final boolean oneIsSufficient;
    private final int allocationPriority;
    private final ItemFunction providedFunction;

    ItemType(boolean oneIsSufficient) {
        this(oneIsSufficient, 0, null);
    }

    ItemType(boolean oneIsSufficient, int allocationPriority) {
        this(oneIsSufficient, allocationPriority, null);
    }

    ItemType(boolean oneIsSufficient, int allocationPriority, ItemFunction providedFunction) {
        this.oneIsSufficient = oneIsSufficient;
        this.allocationPriority = allocationPriority;
        this.providedFunction = providedFunction;
    }

    public boolean isOneIsSufficient() {
        return oneIsSufficient;
    }

    public int getAllocationPriority() {
        return allocationPriority;
    }

    public ItemFunction getProvidedFunction() {
        return providedFunction;
    }
}
