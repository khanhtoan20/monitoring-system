package utils;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.PhysicalMemory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Handler {
    private static Map<String, Executable<String>> handler = new HashMap();

    public Executable<String> get(String key) {
        return handler.get(key);
    }

    public Handler() {
        handler.put("1", Handler::getPhysicalMemory);
    }

    public Double getGigabytesByBytes(Long bytes){
        return (double) (bytes / (1024 * 1024 * 1024));
    }

    private static String getPhysicalMemory(String input){
        SystemInfo si = new SystemInfo();
        GlobalMemory globalMemory = si.getHardware().getMemory();
        List<PhysicalMemory> pmList = globalMemory.getPhysicalMemory();
//        List<Integer> total = pmList.stream().map(item -> getGigabytesByBytes(item.getCapacity())).collect(Collectors.toList());
//        total.forEach(ele->{System.out.println(ele);});
//        long temp = total.stream().reduce(0, (subtotal, element) -> subtotal + element);
        return pmList.toString() + "";
    }

    public static void main(String[] args) {
        Handler temp = new Handler();
        String result = temp.get("1").execute("");
        System.out.println(result);
    }
}
