package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class SrcPropertiesId implements Serializable {

    // источник
    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    // год
    @Column(name = "ssfc_year", nullable = false)
    private Integer year;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SrcPropertiesId that = (SrcPropertiesId) o;

        if (!sourceId.equals(that.sourceId)) return false;
        return year.equals(that.year);
    }

    @Override
    public int hashCode() {
        int result = sourceId.hashCode();
        result = 31 * result + year.hashCode();
        return result;
    }
}
