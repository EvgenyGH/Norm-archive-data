package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity (name = "tariff_zone")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TariffZone {
    @Id
    @Column(nullable = false, name = "zone_id")
    private Integer id;

    @Column(name = "zone_name", nullable = false)
    private String zoneName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TariffZone that = (TariffZone) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
