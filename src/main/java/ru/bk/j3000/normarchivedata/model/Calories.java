package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

//Калорийность топлива
@Entity(name = "calories")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Calories {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "calories_id", nullable = false)
    private UUID id;

    // калорийность
    @Column(name = "calories", nullable = false)
    private Double calories;

    @Column(name = "name", nullable = false)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Calories calories = (Calories) o;

        return id.equals(calories.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
