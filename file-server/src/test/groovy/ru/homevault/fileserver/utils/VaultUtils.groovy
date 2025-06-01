package ru.homevault.fileserver.utils

import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.Path

@Slf4j
class VaultUtils {

    /**
     * Creates a predefined directory structure with files for testing.
     *
     * testRootDir
     * ├─── file1.txt (content1)
     * ├─── file2.txt (content2)
     * ├─── folder1
     * │   ├─── file3.txt (content3)
     * │   └─── subfolder1
     * │       └─── file4.txt (content4)
     * └─── folder2
     *
     * @param testRootDir The root path where the structure should be created.
     * @throws IOException if an I/O error occurs during file/directory creation.
     */
    static void createTestVaultStructure(Path testRootDir) throws IOException {
        Files.createDirectories(testRootDir)
        Files.createFile(testRootDir.resolve("file1.txt"))
        Files.writeString(testRootDir.resolve("file1.txt"), "content1")
        Files.createFile(testRootDir.resolve("file2.txt"))
        Files.writeString(testRootDir.resolve("file2.txt"), "content2")

        Path folder1 = testRootDir.resolve("folder1")
        Files.createDirectories(folder1)
        Files.createFile(folder1.resolve("file3.txt"))
        Files.writeString(folder1.resolve("file3.txt"), "content3")

        Path subfolder1 = folder1.resolve("subfolder1")
        Files.createDirectories(subfolder1)
        Files.createFile(subfolder1.resolve("file4.txt"))
        Files.writeString(subfolder1.resolve("file4.txt"), "content4")

        Path folder2 = testRootDir.resolve("folder2")
        Files.createDirectories(folder2)
    }

    /**
     * Cleans up the specified directory by recursively deleting its contents and the directory itself.
     * Logs information about the cleanup process or warnings if cleanup fails.
     *
     * @param directoryPath The path to the directory to clean up.
     */
    static void cleanupTestDirectory(Path directoryPath) {
        if (directoryPath != null && Files.exists(directoryPath)) {
            try {
                Files.walk(directoryPath)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete)
                log.info "Cleaned up test directory: ${directoryPath}"
            } catch (IOException e) {
                log.warn "Could not completely clean up test directory ${directoryPath}: ${e.message}"
            }
        } else if (directoryPath != null) {
            log.info "Test directory ${directoryPath} does not exist, no cleanup needed."
        }
    }
}
