package ru.bk.j3000.normarchivedata.model.dto;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TariffZoneDTOEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Pattern patternId = Pattern.compile("zoneId=(\\d+)[,)]");
        Pattern patternName = Pattern.compile("zoneName=(.*)[,)]");
        Matcher matcherId = patternId.matcher(text);
        Matcher matcherName = patternName.matcher(text);
        if (matcherId.find() && matcherName.find()) {
            Integer zoneId = Integer.valueOf(matcherId.group(1));
            String zoneName = matcherName.group(1);
            setValue(new TariffZoneDTO(zoneId, zoneName));
        } else {
            super.setAsText(text);
        }
    }
}
