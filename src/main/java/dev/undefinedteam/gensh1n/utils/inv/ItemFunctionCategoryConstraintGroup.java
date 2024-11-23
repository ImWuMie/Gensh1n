package dev.undefinedteam.gensh1n.utils.inv;

import java.util.Objects;

public class ItemFunctionCategoryConstraintGroup extends ItemNumberContraintGroup {
    private final ItemFunction function;

    public ItemFunctionCategoryConstraintGroup(IntRange acceptableRange, int priority, ItemFunction function) {
        super(acceptableRange, priority);
        this.function = function;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ItemFunctionCategoryConstraintGroup that = (ItemFunctionCategoryConstraintGroup) other;
        return function.equals(that.function);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function);
    }
}
