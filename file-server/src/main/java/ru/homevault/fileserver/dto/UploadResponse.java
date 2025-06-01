package ru.homevault.fileserver.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UploadResponse {

    String path;

}
