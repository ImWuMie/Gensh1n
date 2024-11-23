package dev.undefinedteam.gensh1n.utils.inv.items;

import dev.undefinedteam.gensh1n.utils.ComparatorChain;
import dev.undefinedteam.gensh1n.utils.inv.ItemCategory;
import dev.undefinedteam.gensh1n.utils.inv.ItemFunction;
import dev.undefinedteam.gensh1n.utils.inv.ItemType;
import dev.undefinedteam.gensh1n.utils.inv.slots.ItemSlot;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.PREFER_ITEMS_IN_HOTBAR;
import static dev.undefinedteam.gensh1n.utils.inv.ItemCategorization.STABILIZE_COMPARISON;

public class FoodItemFacet extends ItemFacet {
    private static final Comparator<ItemFacet> COMPARATOR =
            new ComparatorChain<>(
                    Comparator.comparingInt(it -> it.getItemStack().getItem() == Items.ENCHANTED_GOLDEN_APPLE ? 1 : 0),
                    Comparator.comparingInt(it -> it.getItemStack().getItem() == Items.GOLDEN_APPLE ? 1 : 0),
//                    // Nutriment
//                    Comparator.comparingDouble(it -> it.getItemStack().getFoodComponent().getSaturation() / (it.itemStack.getFoodComponent().getNutrition())),
//                    Comparator.comparingInt(it -> it.getItemStack().getFoodComponent().getNutrition()),
//                    Comparator.comparingInt(it -> it.getItemStack().getFoodComponent().getSaturation()),
//                    Comparator.comparingInt(it -> it.getItemStack().getCount()),
                    PREFER_ITEMS_IN_HOTBAR,
                    STABILIZE_COMPARISON);

    public FoodItemFacet(ItemSlot itemSlot) {
        super(itemSlot);
    }

    @Override
    public List<Pair<ItemFunction, Integer>> getProvidedItemFunctions() {
        List<Pair<ItemFunction, Integer>> functions = new ArrayList<>();
        if (getItemStack().getFoodComponent() != null) {
            functions.add(new Pair<>(ItemFunction.FOOD, (int)(getItemStack().getCount() * getItemStack().getFoodComponent().getSaturationModifier())));
        }
        return functions;
    }

    @Override
    public ItemCategory getCategory() {
        return new ItemCategory(ItemType.FOOD, 0);
    }

    @Override
    public int compareTo(ItemFacet other) {
        return COMPARATOR.compare(this, (FoodItemFacet) other);
    }
}
