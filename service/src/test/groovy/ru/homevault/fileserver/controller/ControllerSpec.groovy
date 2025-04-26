package ru.homevault.fileserver.controller

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import ru.homevault.fileserver.SpringBootSpec
import ru.homevault.fileserver.utils.VaultUtils
import spock.lang.Shared

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Slf4j
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
            log.info "Setting up test directory: ${testRootDir}"

            cleanupTestDirectory()

            try {
                VaultUtils.createTestVaultStructure(testRootDir)
                log.info "Test directory structure created successfully in ${testRootDir}"
            } catch (IOException e) {
                log.error "Failed to create test directory structure in ${testRootDir}: ${e.message}", e
                throw new RuntimeException("Failed to setup test environment", e)
            }

            setupDone = true
        }
    }

    def cleanupSpec() {
        log.info "Cleaning up test directory after all tests: ${testRootDir}"
        cleanupTestDirectory()
    }

    private void cleanupTestDirectory() {
        if (testRootDir != null && Files.exists(testRootDir)) {
            try {
                Files.walk(testRootDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete)
                log.info "Cleaned up test directory: ${testRootDir}"
            } catch (IOException e) {
                log.warn "Could not completely clean up test directory ${testRootDir}: ${e.message}"
            }
        }
        else if (testRootDir != null) {
            log.info "Test directory ${testRootDir} does not exist, no cleanup needed."
        }
    }
}
