package ru.bk.j3000.normarchivedata.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bk.j3000.normarchivedata.model.Source;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceAlterDTO {
    private UUID sourceId;
    private String sourceName;

    public SourceAlterDTO(Source source) {
        this.sourceId = source.getId();
        this.sourceName = source.getName();
    }
}