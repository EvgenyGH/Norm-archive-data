package ru.bk.j3000.normarchivedata.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceAlterDTO {
    private UUID id;
    private String name;
}