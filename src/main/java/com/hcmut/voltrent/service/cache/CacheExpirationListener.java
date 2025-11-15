package com.hcmut.voltrent.service.cache;

import com.hcmut.voltrent.dtos.model.CacheExpiredEvent;

import java.util.Set;

public interface CacheExpirationListener<T> {

    Set<String> patterns();

    void onCacheExpired(CacheExpiredEvent<T> event);

}
