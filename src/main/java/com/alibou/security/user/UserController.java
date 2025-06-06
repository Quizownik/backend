package com.alibou.security.user;

import com.alibou.security.result.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        try {
            service.changePassword(request, connectedUser);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserStatsResponse> getUserStats(Principal connectedUser) {
        return ResponseEntity.ok(service.getUserStats(connectedUser));
    }

    @GetMapping("/topRankedUsers")
    public ResponseEntity<List<UserRankingResponse>> getTopRankedUsers() {
        return ResponseEntity.ok(service.getTopRankedUsers());
    }
}
