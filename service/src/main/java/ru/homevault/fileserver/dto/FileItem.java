package ru.homevault.fileserver.dto;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class FileItem {

    String name;

    FileType type;

    @Nullable
    Long size;

    LocalDateTime lastModifiedAt;

}
