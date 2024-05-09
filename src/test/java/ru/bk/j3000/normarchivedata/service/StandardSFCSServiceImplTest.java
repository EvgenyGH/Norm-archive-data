package ru.bk.j3000.normarchivedata.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class StandardSFCSServiceImplTest {

    @Test
    @DisplayName("")
    void test() {
        IntStream.iterate(1, i -> i + 7).limit(7 * 4 + 1).forEach(System.out::println);
    }
}
