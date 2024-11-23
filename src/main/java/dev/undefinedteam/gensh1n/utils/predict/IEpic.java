package dev.undefinedteam.gensh1n.utils.predict;

@FunctionalInterface
public interface IEpic<T, E> {
    E get(T t);
}
