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
    private UUID sourceId;
    // Source name
    private String sourceName;
    private Integer year;
    private String branchName;
    private String zoneName;
}
