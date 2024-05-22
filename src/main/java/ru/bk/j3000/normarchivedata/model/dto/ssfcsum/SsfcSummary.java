package ru.bk.j3000.normarchivedata.model.dto.ssfcsum;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.bk.j3000.normarchivedata.model.StandardSFC;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class SsfcSummary {
    private String id;
    private String name;
    private List<SsfcSummary> subSsfcs;
    private List<StandardSFC> allSsfcs;

    // methods

    public boolean hasSub() {
        return Objects.nonNull(subSsfcs) && !subSsfcs.isEmpty();
    }
}
