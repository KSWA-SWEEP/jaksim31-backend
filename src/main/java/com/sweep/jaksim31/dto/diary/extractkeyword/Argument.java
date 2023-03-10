package com.sweep.jaksim31.dto.diary.extractkeyword;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Argument {
    @JsonProperty("analysis_code")
    private final String ANALYSIS_CODE = "ner"; // NOSONAR
    private String text;

}
