package dev.undefinedteam.gensh1n.utils.inv;

import java.util.Objects;

// Assuming ItemCategory is a class that represents a category of items
public class ItemCategory {
    private final ItemType type;
    private final int subtype;

    public ItemCategory(ItemType type, int subtype) {
        this.type = type;
        this.subtype = subtype;
    }

    public ItemType getType() {
        return type;
    }

    public int getSubtype() {
        return subtype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemCategory that = (ItemCategory) o;
        return subtype == that.subtype && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }
}
