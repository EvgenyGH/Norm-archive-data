package ru.bk.j3000.normarchivedata.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.bk.j3000.normarchivedata.model.Source;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceDefinedDTO extends SourceAlterDTO {
    private Boolean defined;

    public SourceDefinedDTO(Source source) {
        super(source);
        this.defined = false;
    }
}
