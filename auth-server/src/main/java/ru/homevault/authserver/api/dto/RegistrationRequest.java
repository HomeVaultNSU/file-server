package ru.homevault.authserver.api.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class RegistrationRequest {

    String username;

    String password;

}
