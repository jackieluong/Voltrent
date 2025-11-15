package com.hcmut.voltrent.dtos.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CacheExpiredEvent <T>{
    private String key;
    private T value;
    private String type;
    private long expiredAt;
}
