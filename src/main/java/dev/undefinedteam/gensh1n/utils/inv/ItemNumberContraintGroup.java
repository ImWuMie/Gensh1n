package dev.undefinedteam.gensh1n.utils.inv;

public abstract class ItemNumberContraintGroup {
    protected final IntRange acceptableRange;
    protected final int priority;

    public ItemNumberContraintGroup(IntRange acceptableRange, int priority) {
        this.acceptableRange = acceptableRange;
        this.priority = priority;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object other);
}

