package ru.bk.j3000.normarchivedata.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SsfcsDTOTests {

    @Test
    @DisplayName("Test get average ssfc.")
    public void havingSsfcShortDTOsGetAverageSsfcThenGetCorrectAverageSsfc() {
        SsfcsDTO ssfcsDTO = new SsfcsDTO();

        List<SsfcShortDTO> shortDTOS = new LinkedList<>();
        SsfcShortDTO dto1 = new SsfcShortDTO();
        dto1.setSsfc(157.25);
        dto1.setProduction(500_000d);

        SsfcShortDTO dto2 = new SsfcShortDTO();
        dto2.setSsfc(156.25);
        dto2.setProduction(500_000d);

        SsfcShortDTO dto3 = new SsfcShortDTO();
        dto3.setSsfc(155.25);
        dto3.setProduction(500_000d);

        Collections.addAll(shortDTOS, dto1, dto2, dto3);

        ssfcsDTO.setSsfcs(shortDTOS);

        assertThat(ssfcsDTO.avgSsfc()).isEqualTo(156.25);
    }

    @Test
    @DisplayName("Test get average ssfcg.")
    public void havingSsfcgShortDTOsGetAverageSsfcgThenGetCorrectAverageSsfcg() {
        SsfcsDTO ssfcsDTO = new SsfcsDTO();

        List<SsfcShortDTO> shortDTOS = new LinkedList<>();
        SsfcShortDTO dto1 = new SsfcShortDTO();
        dto1.setSsfcg(157.25);
        dto1.setGeneration(500_000d);

        SsfcShortDTO dto2 = new SsfcShortDTO();
        dto2.setSsfcg(156.25);
        dto2.setGeneration(500_000d);

        SsfcShortDTO dto3 = new SsfcShortDTO();
        dto3.setSsfcg(155.25);
        dto3.setGeneration(500_000d);

        Collections.addAll(shortDTOS, dto1, dto2, dto3);

        ssfcsDTO.setSsfcs(shortDTOS);

        assertThat(ssfcsDTO.avgSsfcg()).isEqualTo(156.25);
    }
}
