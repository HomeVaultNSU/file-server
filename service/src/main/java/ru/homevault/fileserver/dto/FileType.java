package ru.homevault.fileserver.dto;

public enum FileType {

    FILE,

    DIRECTORY;

    @Override
    public String toString() {
        return this.name().substring(0, 1);
    }
}
