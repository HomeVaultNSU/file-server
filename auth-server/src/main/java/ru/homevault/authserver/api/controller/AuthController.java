package ru.homevault.authserver.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.homevault.authserver.api.dto.DecodedTokenResponse;
import ru.homevault.authserver.api.dto.LoginRequest;
import ru.homevault.authserver.api.dto.RegistrationRequest;
import ru.homevault.authserver.api.dto.TokenResponse;
import ru.homevault.authserver.core.service.JwtService;
import ru.homevault.authserver.core.service.UserService;

@CrossOrigin("*")
@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody RegistrationRequest registrationRequest) {
        userService.registerUser(registrationRequest);
        return ResponseEntity.status(201).build();
    }

    @PutMapping
    public ResponseEntity<TokenResponse> getToken(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(TokenResponse.builder().token(userService.authenticate(loginRequest)).build());
    }

    @GetMapping
    public ResponseEntity<DecodedTokenResponse> decodeToken(@RequestParam String token) {
        return ResponseEntity.ok().body(jwtService.decodeToken(token));
    }

}
