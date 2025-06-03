package ru.homevault.fileserver.mapper

import org.mapstruct.factory.Mappers
import ru.homevault.fileserver.core.mapper.FileMapper
import spock.lang.Specification

import java.time.Instant
import java.time.ZoneId

class FileMapperTest extends Specification {

    static FileMapper MAPPER = Mappers.getMapper(FileMapper)

    def "test mapFileToFileItem with directory"() {
        given:
        def testDir = new File("testDir")
        testDir.mkdir()
        def lastModified = System.currentTimeMillis()
        testDir.setLastModified(lastModified)

        when:
        def result = MAPPER.mapFileToFileItem(testDir)

        then:
        result.type == "D"
        result.size == null
        result.lastModifiedAt == Instant.ofEpochMilli(lastModified).atZone(ZoneId.systemDefault()).toLocalDateTime()

        cleanup:
        testDir.delete()
    }

    def "test mapFileToFileItem with file"() {
        given:
        def testFile = new File("testFile.txt")
        testFile.createNewFile()
        testFile.text = "test content"
        def lastModified = System.currentTimeMillis()
        testFile.setLastModified(lastModified)

        when:
        def result = MAPPER.mapFileToFileItem(testFile)

        then:
        result.type == "F"
        result.size == testFile.length()
        result.lastModifiedAt == Instant.ofEpochMilli(lastModified).atZone(ZoneId.systemDefault()).toLocalDateTime()

        cleanup:
        testFile.delete()
    }
}
