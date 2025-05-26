package top.nustar.nustarmythicmobsextension.myauth.entity;


import top.nustar.nustarmythicmobsextension.myauth.utils.MachineCode;

@lombok.Data
public class Data {
    private String deviceCode;
    private String deviceInfo;
    private String timestamp;
    private String pass;
    private String user;
    private String ckey;
    public Data() {
        this.deviceCode = MachineCode.getMachineCode();
        this.deviceInfo = System.getProperty("os.name") + " " +System.getProperty("os.version");
        this.timestamp = String.valueOf(System.currentTimeMillis() / 1000);
    }

}
