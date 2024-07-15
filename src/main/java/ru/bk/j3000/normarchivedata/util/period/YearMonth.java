package ru.bk.j3000.normarchivedata.util.period;

import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Objects;

@Validated
public record YearMonth(Integer year, Integer month) {
    public YearMonth(Integer year, Integer month) {
        Objects.requireNonNull(year, "Year must be non-null");
        Objects.requireNonNull(month, "Month must be non-null");
        this.year = year >= 1900 ? year : LocalDate.now().getYear();
        this.month = month >= 0 && month <= 12 ? month : 0;
    }


}
