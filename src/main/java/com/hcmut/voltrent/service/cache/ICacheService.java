package com.hcmut.voltrent.service.cache;

import java.util.concurrent.TimeUnit;

public interface ICacheService {
    void put(String key, Object value);

    void put(String key, Object value, long seconds);

    void put(String key, Object value, TimeUnit timeUnit, long time);

    Object get(String key);

    void remove(String key);

    void removeAll();

    void expire(String key, long seconds);

    void expireAt(String key, long unixTime);

    void clearExpire(String key);

    boolean contains(String key);

    long size();

}
