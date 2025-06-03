package ru.homevault.authserver.core.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.homevault.authserver.core.dao.UserDao;
import ru.homevault.authserver.core.entity.User;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final UserDao userDao;

    @Value("${app.jwt.secret-key}")
    private String secretKey;

    private Algorithm algorithm;

    @PostConstruct
    private void init() {
        algorithm = Algorithm.HMAC512(secretKey);
    }

    public String generateToken(long userId) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 1);

        List<String> roles = Collections.singletonList(
                userDao.findById(userId)
                        .map(User::getRole)
                        .orElseThrow(() -> new RuntimeException("User not found"))
        );

        return JWT.create()
                .withSubject("Homevault")
                .withIssuer("Homevault-auth-server")
                .withClaim("role", roles)
                .withClaim("id", userId)
                .withExpiresAt(cal.getTime())
                .sign(algorithm);
    }

}
