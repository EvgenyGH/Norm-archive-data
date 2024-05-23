package ru.bk.j3000.normarchivedata.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Counter {
    private int counter = 0;

    public int getAndIncrement() {
        return ++counter;
    }

    public int increment(int number) {
        counter += number;
        return counter;
    }

    public int getCounter() {
        return counter;
    }
}
