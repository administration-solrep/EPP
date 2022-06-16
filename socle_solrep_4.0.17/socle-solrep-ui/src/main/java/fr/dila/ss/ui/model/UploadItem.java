package fr.dila.ss.ui.model;

import java.io.File;

public class UploadItem {
    private File file;
    private String fileName;
    private int fileSize;

    public UploadItem(File file, String fileName, int fileSize) {
        this.file = file;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
}
