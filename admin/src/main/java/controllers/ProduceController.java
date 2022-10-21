package controllers;

import admin.Admin;
import models.MessageModel;
import models.SystemInfo;
import utils.JSON;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.Map;

import static utils.Command.*;
import static utils.Environment.*;

public class ProduceController {
    private static Map<String, ProduceExecutable<JSON>> controller = new HashMap();

    public static void init() {
        put(COMMAND_LOGIN, ProduceController::login);
        put(COMMAND_GET_ALL_CLIENTS, ProduceController::getClients);
        put(COMMAND_BROADCAST, ProduceController::broadcast);
        put(COMMAND_CLIENT_SYSTEM_INFO, ProduceController::getClientSystemInfo);
    }

    private static String getClientSystemInfo(JSON json) {
        SystemInfo systemInfo = Admin.getSystemInfo();
        return new MessageModel(ADMIN, DEFAULT_SERVER_HOST)
                .put(COMMAND, COMMAND_CLIENT_SYSTEM_INFO)
                .put("cpu", systemInfo.getCpu())
                .put("disk", systemInfo.getDisk())
                .put("ram", systemInfo.getRam())
                .put("os", systemInfo.getOs())
                .json();
    }

    private static String getClients(JSON input) {
        return new MessageModel(ADMIN, DEFAULT_SERVER_HOST)
                .put(COMMAND, COMMAND_GET_ALL_CLIENTS)
                .put("username", "admin")
                .put("password", "123456")
                .json();
    }

    private static String login(JSON input) {
        return new MessageModel(ADMIN, DEFAULT_SERVER_HOST)
                .put("command", "/login")
                .put("username", "admin")
                .put("password", "123456")
                .json();
    }

    private static String broadcast(JSON input) {
        return new MessageModel(ADMIN, DEFAULT_SERVER_HOST)
                .put("command", "/broadcast")
                .put("message", "hello world!")
                .json();
    }

    private static String getNotFoundController(Object input) throws Exception {
        throw new Exception("Invalid Command");
    }

    public static ProduceExecutable get(String key) {
        return controller.getOrDefault(key, ProduceController::getNotFoundController);
    }

    public static ProduceExecutable put(String key, ProduceExecutable<JSON> value) {
        return controller.put(key, value);
    }

    public static void main(String[] args) throws InterruptedException {
//        String tmp1 = System.getenv("PROCESSOR_IDENTIFIER");
//        String tmp2 = System.getenv("PROCESSOR_ARCHITECTURE");
//        String tmp3 = System.getenv("PROCESSOR_ARCHITEW6432");
//        String tmp4 = System.getenv("NUMBER_OF_PROCESSORS");
        com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
//        while (true) {
////            System.out.println(osBean.getProcessCpuLoad() * 100);
//            System.out.println(osBean.getSystemCpuLoad()  * 100);
//
//            Thread.sleep(1000);
//        }
        System.out.println((osBean.getTotalPhysicalMemorySize()/1073741824)+"");
//        console.log(String.valueOf(new File("C:/").getFreeSpace() / (1024 * 1024 * 1024)));

//        System.getenv().forEach((e,v)->{
//            console.log(e+ " | " +v);
//        });
//        console.log( );
//        console.log(tmp2 );
//        console.log(tmp3 );
//        console.log(tmp4 );
        File[] paths;
        FileSystemView fsv = FileSystemView.getFileSystemView();

// returns pathnames for files and directory
        paths = File.listRoots();

// for each pathname in pathname array
        for(File path : paths)
        {
            // prints file and directory paths
            System.out.println("Drive Name: "+path);
            System.out.println("Description: "+ path.getFreeSpace() / (1024 * 1024 * 1024));
        }
//        Properties p = System.getProperties();
//        p.list(System.out);
//        System.out.print("Total CPU:");
//        System.out.println(Runtime.getRuntime().availableProcessors());
//        System.out.println("Max Memory:" + (Runtime.getRuntime().maxMemory() / 1073741824) + "\n" + "available Memory:" + Runtime.getRuntime().freeMemory());
//        System.out.println("os.name=" + System.getProperty("os.name"));
    }
}
