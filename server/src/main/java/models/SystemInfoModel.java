package models;

import com.sun.management.OperatingSystemMXBean;
import utils.Helper;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class SystemInfoModel {
    private static final String UNKNOWN = "Unknown";
    public String ram;
    public String cpu;
    public String disk;
    public String os;
    public String ip;
    public String MAC_address;
    public String hostName;

    public SystemInfoModel(String ram, String cpu, String disk, String os, String ip, String MAC_address, String hostName) {
        this.os = os != null ? os : UNKNOWN;
        this.ip = ip != null ? ip : UNKNOWN;
        this.ram = ram != null ? ram : UNKNOWN;
        this.cpu = cpu != null ? cpu : UNKNOWN;
        this.disk = disk != null ? disk : UNKNOWN;
        this.hostName = hostName != null ? hostName : UNKNOWN;
        this.MAC_address = MAC_address != null ? MAC_address : UNKNOWN;
    }
}
