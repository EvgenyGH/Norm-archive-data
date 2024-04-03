package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "source_properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SourceProperties {
    // источник
    @Id
    @JoinColumn(name = "source_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Source source;

    // год
    @Id
    @Column(name = "ssfc_year", nullable = false)
    private Integer year;

    // филиал
    @Column(name = "source_branch", nullable = false)
    private Integer branch;

    // тарифная зона
    @JoinColumn(name = "zone_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private TariffZone tariffZone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceProperties that = (SourceProperties) o;

        if (!source.equals(that.source)) return false;
        return year.equals(that.year);
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + year.hashCode();
        return result;
    }
}
