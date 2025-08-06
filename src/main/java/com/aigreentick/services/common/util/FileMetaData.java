package com.aigreentick.services.common.util;

import lombok.Data;

@Data
public final class FileMetaData {
    private final String fileName;
    private final long fileSize;
    private final String mimeType;

    public FileMetaData(String fileName, long fileSize, String mimeType) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
    }

}

