package my.application.cobaaws2;

import java.io.File;

public class FileData implements Comparable<FileData> {
    private final String fileName;
    private final String filePath;
    private final long fileSize;
    private final String checksum_hash;

    public FileData(final String fileName, final long fileSize, final String filePath, String checksum_hash) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.checksum_hash = checksum_hash;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getChecksum_hash() {
        return checksum_hash;
    }
// getters

    @Override
    public String toString() {
        return (fileName == null ? "" : fileName) + " - " + fileSize + " - " + filePath + " - " + checksum_hash;
    }

    public boolean isDirectory(){
        File file = new File(filePath);
        return file.isDirectory();
    }
    @Override
    public int compareTo(FileData other) {
        return Long.compare(fileSize, other.fileSize);
    }
}