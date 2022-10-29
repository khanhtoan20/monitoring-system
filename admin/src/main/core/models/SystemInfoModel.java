package models;

import utils.Helper;
import utils.console;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;

public class SystemInfoModel {
    private String ram;
    private String cpu;
    private String disk;
    private String os;

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public SystemInfoModel() {
        this.ram = this.initRAM();
        this.cpu = this.initCPU();
        this.disk = this.initDISK();
        this.os = this.initOS();
    }

    private String initDISK() {
        long diskSpace = 0;
        for(File path : File.listRoots())
        {
            diskSpace += path.getTotalSpace();
        }
        return (diskSpace / 1073741824) + "GB";
    }

    private String initOS() {
        return System.getProperty("os.name");
    }

    private String initRAM() {
        com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        return (osBean.getTotalPhysicalMemorySize()/1073741824) + "GB";
    }

    private String initCPU() {
        String[] cpu = {
                System.getenv("PROCESSOR_IDENTIFIER"),
                System.getenv("PROCESSOR_ARCHITECTURE"),
                System.getenv("PROCESSOR_LEVEL") + " cores",
                System.getenv("NUMBER_OF_PROCESSORS") + " threads"};
        return Arrays.stream(cpu).reduce("", (partialString, element) -> partialString + ", " + element);
    }

    public void shutdown() {
        try {
            Runtime.getRuntime().exec("shutdown -s -t 0");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception{
        Runtime.getRuntime().exec("shutdown -l");
    }
}
