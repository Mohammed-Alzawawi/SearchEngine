package com.example.SearchEngine.utils.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SnowflakeNode {
    private Long step;
    private Long timestamp;
}
