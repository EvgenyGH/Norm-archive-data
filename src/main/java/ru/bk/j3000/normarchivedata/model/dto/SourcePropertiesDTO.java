package ru.bk.j3000.normarchivedata.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourcePropertiesDTO {
    private UUID sourceId;
    private String sourceName;
    private Integer year;
    private Integer branch;
    private String tariffZoneName;
}
