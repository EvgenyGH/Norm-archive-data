package ru.bk.j3000.normarchivedata.util;

@FunctionalInterface
public interface TripleConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
