package ru.bk.j3000.normarchivedata.model.dto;

import java.beans.PropertyEditorSupport;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceAlterDTOEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Pattern patternId = Pattern.compile("sourceId=([a-z0-9-]*)[,)]");
        Pattern patternName = Pattern.compile("sourceName=(.*)[,)]");
        Matcher matcherId = patternId.matcher(text);
        Matcher matcherName = patternName.matcher(text);
        if (matcherId.find() && matcherName.find()) {
            UUID sourceId = UUID.fromString(matcherId.group(1));
            String sourceName = matcherName.group(1);
            setValue(new SourceAlterDTO(sourceId, sourceName));
        } else {
            super.setAsText(text);
        }
    }
}
