package ru.homevault.fileserver.controller

import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class FileControllerSpec extends ControllerSpec {

    // ----------list----------

    def "GET /list - Success Scenarios"(int depth, String path, int expectedItems, int expectedSubdirs) {
        expect:
        mockMvc.perform(get("/list")
                .param("path", path)
                .param("depth", String.valueOf(depth)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.path').value(normalizePath(path)))
                .andExpect(jsonPath('$.items.length()').value(expectedItems))
                .andExpect(jsonPath('$.subdirectories.length()').value(expectedSubdirs))

        where:
        depth | path       | expectedItems | expectedSubdirs
        0     | ""         | 4             | 0
        1     | ""         | 4             | 2
        0     | "/"        | 4             | 0
        1     | "/"        | 4             | 2
        2     | "/"        | 4             | 2 // it only returns the first level of subdirectories
        3     | "/"        | 4             | 2
        0     | "/folder1" | 2             | 0
        1     | "/folder1" | 2             | 1
        2     | "/folder1" | 2             | 1
        1     | "folder1"  | 2             | 1
        1     | "folder1/" | 2             | 1
        0     | "/folder2" | 0             | 0
    }

    def "GET /list - Bad Request with Invalid Path"(int depth, String path) {
        expect:
        mockMvc.perform(get("/list")
                .param("path", path)
                .param("depth", String.valueOf(depth)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.error').exists())

        where:
        depth | path
        -1    | "/"
        0     | "/../"
        0     | "../"
        0     | "/.."
        0     | ".."
    }

    def "GET /list - Bad Request with File Not Found"(int depth, String path) {
        expect:
        mockMvc.perform(get("/list")
                .param("path", path)
                .param("depth", String.valueOf(depth)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.error').exists())

        where:
        depth | path
        0     | "/non_existent_directory/"
        0     | "non_existent_directory/"
        0     | "non_existent_directory"
        0     | "/non_existent_directory"
    }

    def "GET /list - Missing Path Parameter"() {
        expect:
        mockMvc.perform(get("/list").param("depth", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.error').value("Missing required request parameter: path"))
    }

    // ----------upload----------

    def "POST /upload - Success Scenarios"(String path, String expectedStoredPath) {
        given:
        def fileContent = "test upload content".bytes
        def file = new MockMultipartFile("file", "upload.txt", MediaType.TEXT_PLAIN_VALUE, fileContent)
        def targetPath = testRootDir.resolve(expectedStoredPath.substring(1)).normalize()

        when:
        def result = mockMvc.perform(multipart("/upload")
                .file(file)
                .param("path", path)
                .contentType(MediaType.MULTIPART_FORM_DATA))

        then:
        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.path').value(expectedStoredPath))

        // Verify file exists and content is correct
        Files.exists(targetPath)
        Files.readAllBytes(targetPath) == fileContent

        where:
        path       | expectedStoredPath
        "/"        | "/upload.txt"
        "/folder1" | "/folder1/upload.txt"
        "folder1"  | "/folder1/upload.txt"
        "folder1/" | "/folder1/upload.txt"
        "/folder2" | "/folder2/upload.txt"
    }

    def "POST /upload - Empty File"() {
        given:
        def emptyFile = new MockMultipartFile("file", "empty.txt", MediaType.TEXT_PLAIN_VALUE, new byte[0])

        expect:
        mockMvc.perform(multipart("/upload")
                .file(emptyFile)
                .param("path", "/")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.path').value("/file"))
    }

    def "POST /upload - Bad Request Scenarios"(String path) {
        given:
        def fileContent = "test upload content".bytes
        def file = new MockMultipartFile("file", "upload.txt", MediaType.TEXT_PLAIN_VALUE, fileContent)

        expect:
        mockMvc.perform(multipart("/upload")
                .file(file)
                .param("path", path)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.error').exists())

        where:
        path   | _
        "/../" | _
        "../"  | _
        "/.."  | _
        ".."   | _
    }

    // ----------download----------

    def "GET /download - Success Scenarios"(String path, String expectedFilename, String expectedContent) {
        expect:
        mockMvc.perform(get("/download")
                .param("path", path))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename*=UTF-8''" + expectedFilename))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().string(expectedContent))

        where:
        path                            | expectedFilename | expectedContent
        "/file1.txt"                    | "file1.txt"      | "content1"
        "file1.txt"                     | "file1.txt"      | "content1"
        "file1.txt/"                    | "file1.txt"      | "content1"
        "/folder1/file3.txt"            | "file3.txt"      | "content3"
        "folder1/file3.txt"             | "file3.txt"      | "content3"
        "/folder1/subfolder1/file4.txt" | "file4.txt"      | "content4"
    }

    def "GET /download - Bad Request with File Not Found"(String path, expectedStatus) {
        expect:
        mockMvc.perform(get("/download")
                .param("path", path))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.error').exists())

        where:
        path                     | expectedStatus
        "/../"                   | 400
        "../"                    | 400
        "/.."                    | 400
        ".."                     | 400
    }

    def "GET /download - Bad Request with Invalid Path"(String path) {
        expect:
        mockMvc.perform(get("/download")
                .param("path", path))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.error').exists())

        where:
        path                     | _
        "/non_existent_file.txt" | _
        "non_existent_file.txt"  | _
        "/folder1"               | _ // TODO: Should folders really not found? maybe pack them into zips or something? (in the future that is)
        "folder1/"               | _
    }

    def "GET /download - Missing Path Parameter"() {
        expect:
        mockMvc.perform(get("/download"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.error').value("Missing required request parameter: path"))
    }

    private static String normalizePath(String path) {
        String cleanedPath = path.replaceAll("/+", "/")
        if (!cleanedPath.startsWith("/")) {
            cleanedPath = "/" + cleanedPath
        }
        return cleanedPath
    }
}