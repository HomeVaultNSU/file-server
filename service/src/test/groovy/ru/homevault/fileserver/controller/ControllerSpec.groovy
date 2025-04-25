package ru.homevault.fileserver.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import ru.homevault.fileserver.SpringBootSpec
import spock.lang.Shared

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@AutoConfigureMockMvc
class ControllerSpec extends SpringBootSpec {

    @Autowired
    MockMvc mockMvc

    @Value('${app.fs.root-dir}')
    String testRootDirPath

    @Shared
    Path testRootDir

    @Shared
    private boolean setupDone = false

    def setup() {
        if (!setupDone) {
            testRootDir = Paths.get(testRootDirPath).normalize().toAbsolutePath()
            println "Setting up test directory: ${testRootDir}"

            cleanupTestDirectory()

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

            setupDone = true
            /*
            * This creates the following directory structure:
            * testRootDir
            * ├─── file1.txt
            * ├─── file2.txt
            * ├─── folder1
            * │   ├─── file3.txt
            * │   └─── subfolder1
            * │       └─── file4.txt
            * └─── folder2
            */
        }
    }

    def cleanupSpec() {
        println "Cleaning up test directory after all tests: ${testRootDir}"
        cleanupTestDirectory()
    }

    private void cleanupTestDirectory() {
        if (testRootDir != null && Files.exists(testRootDir)) {
            try {
                Files.walk(testRootDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete)
            } catch (IOException e) {
                println "WARN: Could not completely clean up test directory ${testRootDir}: ${e.message}"
            }
        }
    }
}