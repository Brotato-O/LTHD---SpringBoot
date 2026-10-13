package com.example.LTHD.service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    // Lưu jti -> thời gian token hết hạn
    private final Map<String, Date> revokedTokens = new ConcurrentHashMap<>();

    public void revokeToken(String jwtId, Date expirationTime) {
        revokedTokens.put(jwtId, expirationTime);
    }

    public boolean isRevoked(String jwtId) {
        Date expirationTime = revokedTokens.get(jwtId);

        if (expirationTime == null) {
            return false;
        }

        // Token đã hết hạn thì xóa khỏi blacklist
        if (expirationTime.before(new Date())) {
            revokedTokens.remove(jwtId);
            return false;
        }

        return true;
    }
}