package ru.homevault.fileserver.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DirectoryListing {

    String path;

    List<FileItem> items;

    List<DirectoryListing> subdirectories;

}
