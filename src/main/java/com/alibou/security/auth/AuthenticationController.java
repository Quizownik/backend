package com.alibou.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autentykacja", description = "API do zarządzania autentykacją użytkowników")
public class AuthenticationController {

  private final AuthenticationService service;

  @Operation(
          summary = "Rejestracja nowego użytkownika",
          description = "Endpoint pozwalający na rejestrację nowego użytkownika w systemie"
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Rejestracja zakończona sukcesem"),
          @ApiResponse(responseCode = "409", description = "Adres email jest już używany",
                  content = @Content(mediaType = "application/json"))
  })
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    try {
      return ResponseEntity.ok(service.register(request));
    } catch (EmailAlreadyExistsException e) {
      return ResponseEntity
              .status(HttpStatus.CONFLICT)
              .body(Map.of("error", e.getMessage()));
    }
  }

  @Operation(
          summary = "Logowanie użytkownika",
          description = "Endpoint służący do autentykacji użytkownika i uzyskania tokenu JWT"
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Autentykacja zakończona sukcesem"),
          @ApiResponse(responseCode = "401", description = "Nieprawidłowe dane logowania",
                  content = @Content(mediaType = "application/json"))
  })
  @PostMapping("/authenticate")
  public ResponseEntity<?> authenticate(
          @RequestBody AuthenticationRequest request
  ) {
    try {
      return ResponseEntity.ok(service.authenticate(request));
    } catch (BadCredentialsException e) {
      return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED)
              .body(Map.of("error", "Nieprawidłowy login lub hasło"));
    }
  }

  @Operation(
          summary = "Odświeżanie tokenu JWT",
          description = "Endpoint do odświeżania wygasłego tokenu JWT przy pomocy tokenu odświeżającego"
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Token został odświeżony"),
          @ApiResponse(responseCode = "401", description = "Nieprawidłowy token odświeżający")
  })
  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


}
