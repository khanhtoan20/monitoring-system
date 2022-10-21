package models;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;

public class SystemInfo {
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

    public SystemInfo() {
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
                System.getenv("PROCESSOR_LEVEL"),
                System.getenv("PROCESSOR_IDENTIFIER"),
                System.getenv("NUMBER_OF_PROCESSORS"),
                System.getenv("PROCESSOR_ARCHITECTURE")};
        return Arrays.stream(cpu).reduce("", (partialString, element) -> partialString + ", " + element);
    }
}
