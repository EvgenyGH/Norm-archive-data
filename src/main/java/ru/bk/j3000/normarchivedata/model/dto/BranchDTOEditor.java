package ru.bk.j3000.normarchivedata.model.dto;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BranchDTOEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Pattern patternId = Pattern.compile("branchId=(\\d+)[,)]");
        Pattern patternName = Pattern.compile("branchName=(.*)[,)]");
        Matcher matcherId = patternId.matcher(text);
        Matcher matcherName = patternName.matcher(text);
        if (matcherId.find() && matcherName.find()) {
            Integer branchId = Integer.valueOf(matcherId.group(1));
            String branchName = matcherName.group(1);
            setValue(new BranchDTO(branchId, branchName));
        } else {
            super.setAsText(text);
        }
    }
}
