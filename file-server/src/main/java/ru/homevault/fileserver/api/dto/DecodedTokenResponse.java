package ru.homevault.fileserver.api.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class DecodedTokenResponse {

    Long userId;

    String role;

}
