package com.sweep.jaksim31.dto.diary.extractkeyword;

import lombok.Data;

@Data
public class NameEntity {
    final String text;
    final String type;
    Integer count;
    public NameEntity (String text, String type, Integer count) {
        this.text = text;
        this.type = type;
        this.count = count;
    }
}