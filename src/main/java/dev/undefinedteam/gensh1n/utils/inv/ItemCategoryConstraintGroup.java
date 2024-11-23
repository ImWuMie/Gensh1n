package dev.undefinedteam.gensh1n.utils.inv;

import java.util.Objects;

public class ItemCategoryConstraintGroup extends ItemNumberContraintGroup {
    private final ItemCategory category;

    public ItemCategoryConstraintGroup(IntRange acceptableRange, int priority, ItemCategory category) {
        super(acceptableRange, priority);
        this.category = category;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ItemCategoryConstraintGroup that = (ItemCategoryConstraintGroup) other;
        return category.equals(that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category);
    }
}
