package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "sources")
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "source_id", nullable = false)
    private UUID id;

    @Column(name = "source_name", nullable = false)
    @Size(min = 1, max = 50)
    private String name;

    @Column(name = "source_address", nullable = false)
    @Size(min = 1, max = 255)
    private String address;

    // тип источника
    @Column(name = "source_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
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