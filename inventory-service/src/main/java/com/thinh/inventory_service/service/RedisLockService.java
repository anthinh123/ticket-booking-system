package com.thinh.inventory_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisLockService {
    private final StringRedisTemplate redisTemplate;
    private final Duration lockDuration;

    public RedisLockService(StringRedisTemplate redisTemplate,
                            @Value("${app.ticketing.lock-duration}") Duration lockDuration) {
        this.redisTemplate = redisTemplate;
        this.lockDuration = lockDuration;
    }

    public boolean acquireSeatLock(Long seatId, String userId) {
        String lockKey = createRedisLockKey(seatId);

        // This maps exactly to: SET seat:101:lock {userId} NX EX 600
        Boolean isAcquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, userId, lockDuration);

        // Spring returns a Boolean wrapper, so we handle potential nulls safely
        return Boolean.TRUE.equals(isAcquired);
    }

    public void releaseSeatLock(Long seatId) {
        String lockKey = createRedisLockKey(seatId);
        redisTemplate.delete(lockKey);
    }

    private String createRedisLockKey(Long seatId) {
        return "seat:" + seatId + ":lock";
    }
}
