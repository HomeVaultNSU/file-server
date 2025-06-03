package ru.homevault.fileserver.core.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.homevault.fileserver.api.dto.DecodedTokenResponse;
import ru.homevault.fileserver.core.exception.HomeVaultException;

@RequiredArgsConstructor
@Service
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    private final String authServiceUrl = "http://localhost:8090/auth";

    public DecodedTokenResponse decodeToken(String token) {
        String url = UriComponentsBuilder.fromHttpUrl(authServiceUrl)
                .queryParam("token", token)
                .toUriString();

        ResponseEntity<DecodedTokenResponse> response = restTemplate.getForEntity(url, DecodedTokenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new HomeVaultException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        return response.getBody();
    }
}