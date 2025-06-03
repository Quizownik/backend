package com.alibou.security.result;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    // POST – zapis wyniku
    @PostMapping
    public ResponseEntity<String> saveResult(@RequestBody ResultRequest request) {
        resultService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Result created successfully.");
    }

    // GET – lista wyników z pagingiem i sortowaniem
    @GetMapping
    public ResponseEntity<Page<ResultResponse>> getAllResults(
            @PageableDefault(size = 10, sort = "finishedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(resultService.getAll(pageable));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ResultResponse>> getMyResults(
            @PageableDefault(size = 10, sort = "finishedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResultResponse> results = resultService.getResultsForCurrentUser(pageable);
        return ResponseEntity.ok(results);
    }
}