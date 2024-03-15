package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "source_id", nullable = false)
    private UUID id;

    @Column(name = "source_name", nullable = false)
    private String name;

    @Column(name = "source_address", nullable = false)
    private String address;

    // тепловая мощность
    @Column(name = "source_capacity", nullable = false)
    private Float capacity;

    // филиал
    @Column(name = "source_branch", nullable = false)
    private Integer branch;

    // тарифная зона
    @Column(name = "tariff_zone", nullable = false)
    private String tariff_zone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Source source = (Source) o;

        return id.equals(source.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
