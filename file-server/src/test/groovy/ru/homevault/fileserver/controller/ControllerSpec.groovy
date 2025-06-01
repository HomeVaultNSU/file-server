package ru.homevault.fileserver.controller

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Shared
import ru.homevault.fileserver.SpringBootSpec
import ru.homevault.fileserver.utils.VaultUtils

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

            VaultUtils.cleanupTestDirectory(testRootDir)

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
        VaultUtils.cleanupTestDirectory(testRootDir)
    }
}
