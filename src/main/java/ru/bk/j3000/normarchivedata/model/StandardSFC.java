package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.UUID;

/* StandardSpecificFuelConsumption
Результаты расчета удельного расхода условного топлива на отпуск
тепловой энергии с коллекторов источников
*/
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "standard_sfcs",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"properties", "fuelType", "month"})})
public class StandardSFC {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ssfc_id", nullable = false)
    private UUID id;

    // выработка
    @Column(name = "generation", nullable = false)
    @PositiveOrZero
    private Double generation;

    // собственные нужды
    @Column(name = "own_needs", nullable = false)
    @PositiveOrZero
    private Double ownNeeds;

    // отпуск с коллекторов
    @Column(name = "production", nullable = false)
    @PositiveOrZero
    private Double production;

    /*
     нормативный удельный расход условного топлива на отпуск тепловой энергии
     с коллекторов источника
     */
    @Column(name = "standard_sfc", nullable = false)
    @PositiveOrZero
    private Double ssfc;

    /*
        нормативный удельный расход условного топлива на выработку тепловой энергии
        с коллекторов источника
        */
    @Column(name = "standard_sfcg", nullable = false)
    @PositiveOrZero
    private Double ssfcg;

    @JoinColumns({
            @JoinColumn(name = "source_id", referencedColumnName = "source_id", nullable = false),
            @JoinColumn(name = "ssfc_year", referencedColumnName = "ssfc_year", nullable = false)
    })
    @ManyToOne(fetch = FetchType.EAGER)
    private SourceProperty properties;

    // месяц
    @Column(name = "ssfc_month")
    @Min(1)
    @Max(12)
    private Integer month;

    // тип топлива
    @Column(name = "fuel_type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private FUEL_TYPE fuelType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StandardSFC that = (StandardSFC) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}