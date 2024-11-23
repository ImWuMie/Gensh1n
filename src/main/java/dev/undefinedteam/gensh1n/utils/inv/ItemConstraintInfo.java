package dev.undefinedteam.gensh1n.utils.inv;

public class ItemConstraintInfo {
    private final ItemNumberContraintGroup group;
    private final int amountAddedByItem;

    public ItemConstraintInfo(ItemNumberContraintGroup group, int amountAddedByItem) {
        this.group = group;
        this.amountAddedByItem = amountAddedByItem;
    }

    public ItemNumberContraintGroup getGroup() {
        return group;
    }

    public int getAmountAddedByItem() {
        return amountAddedByItem;
    }
}
