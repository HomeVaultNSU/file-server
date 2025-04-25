package ru.homevault.fileserver.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.homevault.fileserver.dto.DirectoryListing;
import ru.homevault.fileserver.dto.UploadResponse;
import ru.homevault.fileserver.exception.HomeVaultException;
import ru.homevault.fileserver.service.FileService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@CrossOrigin("*")
@Validated
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/list")
    public DirectoryListing list(
            @RequestParam("path") @NotBlank(message = "Path cannot be blank") String path,
            @RequestParam(value = "depth", defaultValue = "0") @Min(value = 0, message = "Depth must be >= 0") Integer depth
    ) {
        return fileService.getDirectoryListing(path, depth);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> upload(
            @RequestPart("file") @NotNull(message = "File cannot be null") MultipartFile file,
            @RequestParam(value = "path", defaultValue = "/") @NotBlank(message = "Path cannot be blank") String path
    ) {
        String filePath = fileService.uploadFile(file, path);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UploadResponse.builder().path(filePath).build());
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(
            @RequestParam("path") @NotBlank(message = "Path cannot be blank") String filePath
    ) {
        Resource fileResource = fileService.downloadFile(filePath);

        String encodedFilename = Optional.ofNullable(fileResource.getFilename())
                .map(filename -> URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20"))
                .orElseThrow(() -> new HomeVaultException("Invalid file name!", HttpStatus.BAD_REQUEST));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileResource);
    }
}