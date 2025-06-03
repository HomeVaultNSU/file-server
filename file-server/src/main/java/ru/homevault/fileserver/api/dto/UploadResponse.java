package ru.homevault.fileserver.api.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UploadResponse {

    String path;

}
