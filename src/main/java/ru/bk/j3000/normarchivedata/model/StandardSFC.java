package ru.bk.j3000.normarchivedata.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/* StandardSpecificFuelConsumption
Результаты расчета удельного расхода условного топлива на отпуск
тепловой энергии с коллекторов источников
*/
@Entity(name = "standardSFC")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StandardSFC {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ssfc_id", nullable = false)
    private UUID id;

    // источник
    @JoinColumn(name = "source_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Source sourceId;

    // выработка
    @Column(name = "generation", nullable = false)
    private Double generation;

    // собственные нужды
    @Column(name ="own_needs", nullable = false)
    private Double ownNeeds;

    // отпуск с коллекторов
    @Column(name = "production", nullable = false)
    private Double production;

    // тип топлива
    @Column(name = "fuel_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FUEL_TYPE fuelType;

    /*
    нормативный удельный расход условного топлива на отпуск тепловой энергии
    с коллекторов источника
    */
    @Column(name = "standard_sfc", nullable = false)
    private Double ssfc;

    /*
    нормативный удельный расход условного топлива на выработку тепловой энергии
    с коллекторов источника
    */
    @Column(name = "standard_sfcg", nullable = false)
    private Double ssfcg;

    // потребление топлива
    @Column(name = "fuel_consumption", nullable = false)
    private Double fuelConsumption;

    // потребление условного топлива
    @Column(name = "fuel_consumption_std", nullable = false)
    private Double fuelConsumptionStd;

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