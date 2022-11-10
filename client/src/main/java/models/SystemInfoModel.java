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
    private OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private static final String WMIC_CPU_GET_LOAD_PERCENTAGE = "wmic cpu get loadPercentage";
    private static final String SYSTEM_PROPERTY_OS_NAME = "os.name";
    private static final String PHYSIC_MEMORY_UNIT = "GB";
    private static final String UNKNOWN = "Unknown";
    private static final Integer GB = 1073741824;

    public String ram;
    public String cpu;
    public String disk;
    public String os;
    public String ip;
    public String MAC_address;
    public String hostName;

    public SystemInfoModel() {
        this.initSystemInfo();
    }

    private void initSystemInfo() {
        this.os = this.initOS();
        this.ip = this.initIP();
        this.cpu = this.initCPU();
        this.ram = this.initRAM();
        this.disk = this.initDISK();
        this.hostName = this.initHostName();
        this.MAC_address = this.initMACAddress();
    }

    private String initHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    private String initDISK() {
        long diskSpace = 0;
        for (File path : File.listRoots()) {
            diskSpace += path.getTotalSpace();
        }
        return (diskSpace / 1073741824) + "GB";
    }

    private String initOS() {
        return System.getProperty(SYSTEM_PROPERTY_OS_NAME);
    }

    private String initRAM() {
        return (osBean.getTotalPhysicalMemorySize() / GB) + PHYSIC_MEMORY_UNIT;
    }

    private String initCPU() {
        String[] cpu = {
                System.getenv("PROCESSOR_LEVEL"),
                System.getenv("PROCESSOR_IDENTIFIER"),
                System.getenv("NUMBER_OF_PROCESSORS"),
                System.getenv("PROCESSOR_ARCHITECTURE")};
        return String.format("%s, %s, %s cores, %s threads", cpu[1], cpu[3], cpu[0], cpu[2]);
    }
    public static String getIPHost(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
    private String initIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    private String initMACAddress() {
        try {
            byte[] hardwareAddress = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            String[] hexadecimalFormat = new String[hardwareAddress.length];
            for (int i = 0; i < hardwareAddress.length; i++) {
                hexadecimalFormat[i] = String.format("%02X", hardwareAddress[i]);
            }
            return String.join("-", hexadecimalFormat);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    public String getMemoryLoadPercentage() {
        double monitor = osBean.getFreePhysicalMemorySize();
        double tmp = osBean.getTotalPhysicalMemorySize();
        double res = monitor / tmp;
        return Math.round(res * 100) + "";
    }

    public String getProcessorLoadPercentage() {
        return Helper.CommandPrompt(WMIC_CPU_GET_LOAD_PERCENTAGE);
    }

    public String getDriveLoadPercentage() {
        double totalSpace = 0;
        double totalFree = 0;
        for (File path : File.listRoots()) {
            totalSpace += path.getTotalSpace();
            totalFree += path.getFreeSpace();
        }
        double totalUsage = totalSpace - totalFree;
        return Math.round(totalUsage / totalSpace * 100) + "";
    }

    public String getClipboard() {
        try {
            return Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return UNKNOWN;
        }
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

    @Override
    public String toString() {
        return "SystemInfoModel{" +
                "osBean=" + osBean +
                ", ram='" + ram + '\'' +
                ", cpu='" + cpu + '\'' +
                ", disk='" + disk + '\'' +
                ", os='" + os + '\'' +
                ", ip='" + ip + '\'' +
                ", MAC_address='" + MAC_address + '\'' +
                ", hostName='" + hostName + '\'' +
                '}';
    }
}
