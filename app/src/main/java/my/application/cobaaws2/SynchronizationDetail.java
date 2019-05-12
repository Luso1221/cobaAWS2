package my.application.cobaaws2;

import java.sql.Timestamp;

public class SynchronizationDetail implements Comparable<SynchronizationDetail>{
    private Timestamp timestamp;
    private Integer status;
    private FileData fileData;

    public SynchronizationDetail(){

    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public SynchronizationDetail(Timestamp timestamp, FileData fileData) {
        this.timestamp = timestamp;
        this.fileData = fileData;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public FileData getFileData() {
        return fileData;
    }

    public void setFileData(FileData fileData) {
        this.fileData = fileData;
    }

    @Override
    public int compareTo(SynchronizationDetail detail) {
        return timestamp.compareTo(detail.timestamp);
    }
}
