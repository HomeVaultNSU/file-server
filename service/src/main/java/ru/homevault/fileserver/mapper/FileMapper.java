package ru.homevault.fileserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.homevault.fileserver.dto.FileItem;
import ru.homevault.fileserver.dto.FileType;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper
public interface FileMapper {

    @Mapping(target = "type", source = ".", qualifiedByName = "mapFileToFileType")
    @Mapping(target = "size", source = ".", qualifiedByName = "mapFileToSize")
    @Mapping(target = "lastModifiedAt", source = ".", qualifiedByName = "mapFileToLastModifiedAt")
    FileItem mapFileToFileItem(File file);

    @Named("mapFileToFileType")
    default String mapFileToFileType(File file) {
        return (file.isDirectory() ? FileType.DIRECTORY : FileType.FILE).name().substring(0, 1);
    }

    @Named("mapFileToSize")
    default Long mapFileToSize(File file) {
        return file.isDirectory() ? null : file.length();
    }

    @Named("mapFileToLastModifiedAt")
    default LocalDateTime mapFileToLastModifiedAt(File file) {
        return Instant.ofEpochMilli(file.lastModified())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }

}
