package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "source_properties")
public class SourceProperty {
    @EmbeddedId
    private SrcPropertiesId id;

    // филиал
    @Column(name = "source_branch", nullable = false)
    @Size(min = 1, max = 30)
    private Integer branch;

    // тарифная зона
    @JoinColumn(name = "zone_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private TariffZone tariffZone;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SourceProperty that)) return false;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
