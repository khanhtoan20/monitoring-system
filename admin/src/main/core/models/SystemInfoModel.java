package models;

import java.util.Vector;

public class SystemInfoModel {
    private static final String UNKNOWN = "Unknown";
    public String ram;
    public String cpu;
    public String disk;
    public String os;
    public String ip;
    public String MAC_address;
    public String hostName;

    private String get(Vector<String> vector, Integer index) {
        try {
            return vector.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    public SystemInfoModel(String ram, String cpu, String disk, String os, String ip, String MAC_address, String hostName) {
        this.os = os != null ? os : UNKNOWN;
        this.ip = ip != null ? ip : UNKNOWN;
        this.ram = ram != null ? ram : UNKNOWN;
        this.cpu = cpu != null ? cpu : UNKNOWN;
        this.disk = disk != null ? disk : UNKNOWN;
        this.hostName = hostName != null ? hostName : UNKNOWN;
        this.MAC_address = MAC_address != null ? MAC_address : UNKNOWN;
    }

    public String getRam() {
        return ram;
    }

    public String getCpu() {
        return cpu;
    }

    public String getDisk() {
        return disk;
    }

    public String getOs() {
        return os;
    }

    public String getIp() {
        return ip;
    }

    public String getMAC_address() {
        return MAC_address;
    }

    public String getHostName() {
        return hostName;
    }
}
