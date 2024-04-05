package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class SrcPropertiesId implements Serializable {

    // источник
    @JoinColumn(name = "source_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Source source;

    // год
    @Column(name = "ssfc_year", nullable = false)
    private Integer year;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SrcPropertiesId that = (SrcPropertiesId) o;

        if (!Objects.equals(source, that.source)) return false;
        return Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (year != null ? year.hashCode() : 0);
        return result;
    }
}
