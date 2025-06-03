package ru.homevault.authserver.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.homevault.authserver.api.dto.LoginRequest;
import ru.homevault.authserver.api.dto.RegistrationRequest;
import ru.homevault.authserver.core.dao.UserDao;
import ru.homevault.authserver.core.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.homevault.authserver.core.exception.HomeVaultException;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserDao userDao;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public void registerUser(RegistrationRequest registrationRequest) {
        if (userDao.existsByUsername(registrationRequest.getUsername())) {
            throw new HomeVaultException("Username is already taken", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setRole("USER_ROLE");
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        userDao.save(user);
    }

    public final String authenticate(LoginRequest loginRequest) {
        User user = userDao.findByUsername(loginRequest.getUsername())
                .filter(u -> passwordEncoder.matches(loginRequest.getPassword(), u.getPassword()))
                .orElseThrow(() -> new HomeVaultException("Bad username or password", HttpStatus.BAD_REQUEST));

        return jwtService.generateToken(user.getId());
    }

}
