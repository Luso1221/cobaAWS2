package my.application.cobaaws2;

import java.sql.Timestamp;
import java.util.List;

public class Synchronization implements Comparable<Synchronization>{
    private Timestamp timestamp_start, timestamp_end;
    private int sync_id, status, profile_id;
    private List<SynchronizationDetail> synchronizationDetails;
    public Synchronization(){
    }
    public Synchronization(Timestamp timestamp_start, Timestamp timestamp_end, int sync_id, int status) {
        this.timestamp_start = timestamp_start;
        this.timestamp_end = timestamp_end;
        this.sync_id = sync_id;
        this.status = status;
    }

    public void setSynchronizationDetails(List<SynchronizationDetail> synchronizationDetails) {
        this.synchronizationDetails = synchronizationDetails;
    }

    public int getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
    }

    public Timestamp getTimestamp_end() {
        return timestamp_end;
    }

    @Override
    public String toString() {
        return "Synchronization{" +
                "timestamp_start=" + timestamp_start +
                ", timestamp_end=" + timestamp_end +
                ", sync_id=" + sync_id +
                ", profile_id=" + status +
                ", synchronizationDetails=" + synchronizationDetails +
                '}';
    }

    public List<SynchronizationDetail> getSynchronizationDetails() {
        return synchronizationDetails;
    }

    public void setTimestamp_end(Timestamp timestamp_end) {
        this.timestamp_end = timestamp_end;
    }

    public int getSync_id() {
        return sync_id;
    }

    public void setSync_id(int sync_id) {
        this.sync_id = sync_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getTimestamp_start() {
        return timestamp_start;
    }

    public void setTimestamp_start(Timestamp timestamp_start) {
        this.timestamp_start = timestamp_start;
    }

    @Override
    public int compareTo(Synchronization sync) {
        return this.timestamp_start.compareTo(sync.timestamp_start);
    }
}
