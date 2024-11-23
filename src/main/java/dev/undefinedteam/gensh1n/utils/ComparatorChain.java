package dev.undefinedteam.gensh1n.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComparatorChain<T> implements Comparator<T> {
    private final Comparator<T>[] comparisonFunctions;

    @SafeVarargs
    public ComparatorChain(Comparator<T>... comparisonFunctions) {
        this.comparisonFunctions = comparisonFunctions;
    }

    @Override
    public int compare(T o1, T o2) {
        for (Comparator<T> comparisonFunction : this.comparisonFunctions) {
            int comparisonResult = comparisonFunction.compare(o1, o2);
            if (comparisonResult != 0) {
                return comparisonResult;
            }
        }
        return 0;
    }



    @SafeVarargs
    public static <T> ComparatorChain<T> builder(Comparator<T>... comparisonFunctions) {
        return new ComparatorChain<>(comparisonFunctions);
    }

    public static <T> ComparatorChain.Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        public List<Comparator<T>> comparators = new ArrayList<>();

        public Builder<T> compare(Comparator<T> comparator) {
            this.comparators.add(comparator);
            return this;
        }
        public Builder<T> thenComparing(Comparator<T> comparator) {
            return this.compare(comparator);
        }

        public ComparatorChain<T> build() {
            return new ComparatorChain<>(this.comparators.toArray(Comparator[]::new));
        }
    }
}
