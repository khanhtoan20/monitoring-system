package controllers;

import models.MessageModel;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.PhysicalMemory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static utils.Command.*;
import static utils.Environment.*;

public class Controller {
    private static SystemInfo si = new SystemInfo();

    private static Map<String, Executable> controller = new HashMap();

    public static void init() {
        put(COMMAND_RAM, Controller::getPhysicalMemory);
    }

    private static String getPhysicalMemory(Object input) {
        GlobalMemory globalMemory = si.getHardware().getMemory();
        List<PhysicalMemory> pm = globalMemory.getPhysicalMemory();
        List<Integer> pmValues = pm.stream().map(item -> (int) getGigabytesByBytes(item.getCapacity())).collect(Collectors.toList());
        long result = pmValues.stream().reduce(0, (subtotal, element) -> subtotal + element);
        return new MessageModel("", DEFAULT_SERVER_HOST, result + "GB").json();
    }

    private static String getNotFoundController(Object input) {
        return new MessageModel("", DEFAULT_SERVER_HOST, "STATUS_MESSAGE_403").json();
    }

    private static long getGigabytesByBytes(Long bytes) {
        return (long) (bytes / (1024 * 1024 * 1024));
    }

    public static Executable get(String key) {
        return controller.getOrDefault(key, Controller::getNotFoundController);
    }

    public static Executable put(String key, Executable value) {
        return controller.put(key, value);
    }
}
