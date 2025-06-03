package ru.homevault.authserver.core.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.homevault.authserver.api.dto.DecodedTokenResponse;
import ru.homevault.authserver.core.dao.UserDao;
import ru.homevault.authserver.core.entity.User;
import ru.homevault.authserver.core.exception.HomeVaultException;

import java.util.Calendar;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final UserDao userDao;

    @Value("${app.jwt.secret-key}")
    private String secretKey;

    @Value("${app.jwt.leeway}")
    private Long leeway;

    private Algorithm algorithm;

    @PostConstruct
    private void init() {
        algorithm = Algorithm.HMAC512(secretKey);
    }

    public String generateToken(long userId) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);

        String role = userDao.findById(userId)
                .map(User::getRole)
                .orElseThrow(() -> new HomeVaultException("User not found", HttpStatus.NOT_FOUND));

        return JWT.create()
                .withSubject("Homevault")
                .withIssuer("Homevault-auth-server")
                .withClaim("role", role)
                .withClaim("id", userId)
                .withExpiresAt(cal.getTime())
                .sign(algorithm);
    }

    public DecodedTokenResponse decodeToken(String token) {
        Algorithm algorithm = Algorithm.HMAC512(secretKey);

        JWTVerifier verifier = JWT.require(algorithm).acceptExpiresAt(leeway).build();

        DecodedJWT decodedJwt = verifier.verify(token);

        return DecodedTokenResponse.builder()
                .userId(decodedJwt.getClaim("id").asLong())
                .role(decodedJwt.getClaim("role").asString())
                .build();
    }
}
