package ru.homevault.fileserver.api.dto;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class FileItem {

    String name;

    String type;

    @Nullable
    Long size;

    LocalDateTime lastModifiedAt;

    public boolean isDirectory() {
        return this.type.equals("D");
    }

}
