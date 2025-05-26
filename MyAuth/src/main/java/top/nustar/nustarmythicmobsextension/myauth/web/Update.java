package top.nustar.nustarmythicmobsextension.myauth.web;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import top.nustar.nustarmythicmobsextension.myauth.entity.Data;
import top.nustar.nustarmythicmobsextension.myauth.utils.Sign;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Update {
    public JSONObject jsonObject = new JSONObject();
    public JSONObject dataObject = new JSONObject();
    public Data data;

    @Getter
    private final JSONObject result;
    public Update(){
        data = new Data();
        this.dataObject.put("device_info",this.data.getDeviceInfo());
        this.dataObject.put("device_code",this.data.getDeviceCode());
        this.dataObject.put("timestamp",this.data.getTimestamp());
        this.jsonObject.put("data",dataObject);
        this.jsonObject.put("skey","ff04bfa8-35d9-4a2c-b1b6-fcfc0d4663d3");
        this.jsonObject.put("vkey","17C0B9BD-C72D-4817-859C-CD97036E5256");
        this.jsonObject.put("sign", Sign.calculateSign(this.dataObject,"woshinidie666666"));
        result = JSONObject.parseObject(response().getBody(), JSONObject.class);
    }
    public HttpResponse<String> response(){
        return Unirest.post("http://47.103.131.3:7943/myauth/soft/checkUpdate")
                .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .header("Content-Type", "application/json")
                .body(this.jsonObject)
                .asString();
    }
    public List<String> getUpdateLog(JSONObject newVerData) {
        String updLog = newVerData.get("updLog").toString();
        return new ArrayList<>(Arrays.asList(updLog.split("\\n")));
    }
}
