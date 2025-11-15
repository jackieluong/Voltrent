package com.hcmut.voltrent.service.cache;

import com.github.benmanes.caffeine.cache.*;
import com.hcmut.voltrent.dtos.model.CacheExpiredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@com.hcmut.voltrent.annotations.Caffeine
@Slf4j
public class CaffeineCacheService implements ICacheService {

    private final Cache<String, CacheEntry> cache;
    private final List<CacheExpirationListener<?>> listeners = new ArrayList<>();
    private long defaultTtlSeconds = 600;

    public CaffeineCacheService() {
        this.cache = Caffeine.newBuilder()
                .maximumSize(10000)
                .scheduler(Scheduler.systemScheduler())
                .expireAfter(new Expiry<String, CacheEntry>() {
                    @Override
                    public long expireAfterCreate(String key, CacheEntry entry, long currentTime) {
                        return TimeUnit.SECONDS.toNanos(entry.getTtlSeconds());
                    }

                    @Override
                    public long expireAfterUpdate(String key, CacheEntry entry, long currentTime, long currentDuration) {
                        return TimeUnit.SECONDS.toNanos(entry.getTtlSeconds());
                    }

                    @Override
                    public long expireAfterRead(String key, CacheEntry entry, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .evictionListener(((key, cacheEntry, cause) -> {
                    if (RemovalCause.EXPIRED.equals(cause)) {
                        log.warn("Cache Expired with [key={}] [value={}]", key, cacheEntry.getValue());
                        CacheExpiredEvent cacheExpiredEvent = new CacheExpiredEvent<>(key, cacheEntry.getValue(),
                                cause.toString(), System.currentTimeMillis());
                        listeners.forEach(l -> l.onCacheExpired(cacheExpiredEvent));
                    }
                }))
                .build();
    }

    public void addListener(CacheExpirationListener<?> listener) {
        listeners.add(listener);
    }

    @Override
    public void put(String key, Object value) {
        put(key, value, defaultTtlSeconds);
    }

    @Override
    public void put(String key, Object value, long seconds) {
        put(key, value, TimeUnit.SECONDS, seconds);
    }

    @Override
    public void put(String key, Object value, TimeUnit timeUnit, long time) {
        cache.put(key, new CacheEntry(value, timeUnit.toSeconds(time)));
        log.info("Put [key={}] [value={}] [ttl={} {}]", key, value, time, timeUnit.name());
    }

    @Override
    public Object get(String key) {
        CacheEntry entry = cache.getIfPresent(key);
        return Optional.ofNullable(entry)
                .map(CacheEntry::getValue)
                .orElse(null);
    }

    @Override
    public void remove(String key) {
        cache.invalidate(key);
    }

    @Override
    public void removeAll() {
        cache.invalidateAll();
    }

    @Override
    public void expire(String key, long seconds) {
        CacheEntry entry = cache.getIfPresent(key);
        if (entry != null) {
            put(key, entry.getValue(), seconds);
        }
    }

    @Override
    public void expireAt(String key, long unixTime) {
        CacheEntry entry = cache.getIfPresent(key);
        if (entry != null) {
            long currentTime = System.currentTimeMillis() / 1000;
            long seconds = unixTime - currentTime;
            if (seconds > 0) {
                expire(key, seconds);
            } else {
                remove(key);
            }
        }
    }

    @Override
    public void clearExpire(String key) {
        CacheEntry entry = cache.getIfPresent(key);
        if (entry != null) {
            cache.put(key, new CacheEntry(entry.getValue(), Long.MAX_VALUE));
        }
    }

    @Override
    public boolean contains(String key) {
        return cache.getIfPresent(key) != null;
    }

    @Override
    public long size() {
        return cache.estimatedSize();
    }

    private static class CacheEntry {
        private final Object value;
        private final long ttlSeconds;

        public CacheEntry(Object value, long ttlSeconds) {
            this.value = value;
            this.ttlSeconds = ttlSeconds;
        }

        public Object getValue() {
            if (value == null) {
                return new Object();
            }
            return value;
        }

        public long getTtlSeconds() {
            return ttlSeconds;
        }
    }
}
