package com.alibou.security.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
@Tag(name = "Odpowiedzi", description = "API do zarządzania odpowiedziami na pytania")
@SecurityRequirement(name = "bearerAuth")
public class AnswerController {

//    private final AnswerService service;
//
//    @Operation(
//            summary = "Zapisanie odpowiedzi",
//            description = "Endpoint pozwalający na zapisanie odpowiedzi na pytanie"
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "202", description = "Odpowiedź została zapisana pomyślnie")
//    })
//    @PostMapping
//    public ResponseEntity<?> save(
//            @RequestBody AnswerRequest request
//    ){
//        service.save(request);
//        return ResponseEntity.accepted().build();
//    }
//
//    @Operation(
//            summary = "Pobieranie wszystkich odpowiedzi",
//            description = "Endpoint pozwalający na pobranie listy wszystkich odpowiedzi"
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Zwrócono listę wszystkich odpowiedzi")
//    })
//    @GetMapping
//    public ResponseEntity<List<Answer>> findAll(){return ResponseEntity.ok(service.findAll());}
}
