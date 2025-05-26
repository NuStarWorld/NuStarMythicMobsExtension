package top.nustar.nustarmythicmobsextension.myauth.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Formatter;

public class MachineCode {
    public static String getMachineCode() {
        try {
            String cpuId = getCPUId();
            String boardId = getBoardId();
            String macAddress = getMacAddress();

            String machineCode = cpuId + "-" + boardId + "-" + macAddress;
            return sha1(machineCode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getCPUId() {
        // Implement your CPU ID retrieval logic here
        // For Windows: You can use WMI to query Win32_Processor
        // For Linux: You can use "dmidecode" command

        // For demonstration purposes, returning a placeholder value
        return "123456";
    }

    private static String getBoardId() {
        // Implement your mainboard ID retrieval logic here
        // For Windows: You can use WMI to query Win32_BaseBoard
        // For Linux: You can use "dmidecode" command

        // For demonstration purposes, returning a placeholder value
        return "789012";
    }

    private static String getMacAddress() throws Exception {
        InetAddress ip = InetAddress.getLocalHost();
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);

        byte[] mac = network.getHardwareAddress();

        StringBuilder macAddress = new StringBuilder();
        Formatter formatter = new Formatter();
        try {
            for (int i = 0; i < mac.length; i++) {
                macAddress.append(formatter.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
        } catch (NullPointerException e) {
            return "SpecialEnvironment";
        }

        return macAddress.toString();
    }

    private static String sha1(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] byteData = md.digest(data.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : byteData) {
            hexString.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return hexString.toString();
    }
}
