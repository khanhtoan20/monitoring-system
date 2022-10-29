package models;

public class ClientModel {
    private String os;
    private String cpu;
    private String ram;
    private String disk;
    private String ip;

    public ClientModel(String os, String cpu, String ram, String disk, String ip) {
        this.os = os;
        this.cpu = cpu;
        this.ram = ram;
        this.disk = disk;
        this.ip = ip;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
