package ru.bk.j3000.normarchivedata.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Counter {
    private int counter = 0;

    public int incrementAndGet() {
        return counter++;
    }

    public int getAndIncrement(int increment) {
        int oldCounter = counter;
        counter += increment;

        return oldCounter;
    }

    public void increment(int number) {
        counter += number;
    }

    public int getCounter() {
        return counter;
    }
}
