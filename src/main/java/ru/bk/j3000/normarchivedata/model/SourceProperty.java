package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    private SrcPropertyId id;

    // филиал
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", nullable = false)
    @NotNull
    private Branch branch;

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
