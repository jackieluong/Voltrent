package com.hcmut.voltrent.service.cache;

import com.hcmut.voltrent.dtos.model.CacheExpiredEvent;

public interface CacheExpirationListener<T> {

    void onCacheExpired(CacheExpiredEvent<T> event);

}
