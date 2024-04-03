package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity (name = "source")
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

    // тип источника
    @Column(name = "source_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SOURCE_TYPE sourceType;

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
