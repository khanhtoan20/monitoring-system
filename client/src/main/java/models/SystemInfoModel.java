package models;

import com.sun.management.OperatingSystemMXBean;
import utils.Helper;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;

public class SystemInfoModel {
    private OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private static final String WMIC_CPU_GET_LOAD_PERCENTAGE = "wmic cpu get loadPercentage";
    private static final Integer GB = 1073741824;

    private String ram;
    private String cpu;
    private String disk;
    private String os;
    private String ip;

    public SystemInfoModel() {
        this.os = this.initOS();
        this.ip = this.initIP();
        this.cpu = this.initCPU();
        this.ram = this.initRAM();
        this.disk = this.initDISK();
    }

    private String initDISK() {
        long diskSpace = 0;
        for (File path : File.listRoots()) {
            diskSpace += path.getTotalSpace();
        }
        return (diskSpace / 1073741824) + "GB";
    }

    private String initOS() {
        return System.getProperty("os.name");
    }

    private String initRAM() {
        return (osBean.getTotalPhysicalMemorySize() / GB) + "GB";
    }

    private String initCPU() {
        String[] cpu = {
                System.getenv("PROCESSOR_LEVEL"),
                System.getenv("PROCESSOR_IDENTIFIER"),
                System.getenv("NUMBER_OF_PROCESSORS"),
                System.getenv("PROCESSOR_ARCHITECTURE")};
        return String.format("%s, %s, %s cores, %s threads", cpu[1], cpu[3], cpu[0], cpu[2]);
    }

    private String initIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "NaN";
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
            return Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.getTextPlainUnicodeFlavor()).toString();
        } catch (Exception e) {
            return null;
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
}
