package com.alibou.security.user;

import com.alibou.security.result.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
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
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserStatsResponse> getUserStats(@PathVariable Integer userId) {
        return ResponseEntity.ok(service.getUserStats(userId));
    }



    @GetMapping("/topRankedUsers")
    public ResponseEntity<List<UserRankingResponse>> getTopRankedUsers() {
        return ResponseEntity.ok(service.getTopRankedUsers());
    }
}
