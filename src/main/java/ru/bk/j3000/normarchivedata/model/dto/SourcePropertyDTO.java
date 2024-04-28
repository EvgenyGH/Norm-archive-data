package ru.bk.j3000.normarchivedata.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourcePropertyDTO {
    // Source id
    private UUID id;
    // Source name
    private String name;
    private Integer year;
    private Integer branch;
    private String zoneName;
}
