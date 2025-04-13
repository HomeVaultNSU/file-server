package ru.homevault.fileserver.service;

import ru.homevault.fileserver.dto.DirectoryListing;
import ru.homevault.fileserver.dto.FileItem;

import java.util.List;

public interface FileService {

    List<FileItem> getDirectoryContent(String path);

    DirectoryListing getDirectoryListing(String path, int depth);

}
