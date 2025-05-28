package com.alibou.security.user;

public record UserRankingResponse(
        String username,
        Integer score
) {}
