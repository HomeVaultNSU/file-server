package ru.homevault.fileserver.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.homevault.fileserver.dto.DirectoryListing;
import ru.homevault.fileserver.service.FileService;

@CrossOrigin("*")
@Validated
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/list")
    public DirectoryListing list(
            @RequestParam("path") String path,
            @RequestParam(value = "depth", defaultValue = "0") @Positive Integer depth
    ) {
        return fileService.getDirectoryListing(path, depth);
    }

}
