package top.nustar.nustarmythicmobsextension.myauth.utils;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sign {
    /**
     * 计算sign签名
     *
     * @param jsonObject json对象
     * @param genKey 计算sign
     * @return 返回sigh
     */
    public static String calculateSign(JSONObject jsonObject, String genKey) {
        String pathvalue = json2pathValue(jsonObject);
        pathvalue = pathvalue + "&gen_key=" + genKey;
        pathvalue = pathvalue.replaceAll("\\\\r\\\\n","\\\\n").replaceAll("\\\\n","\\\\r\\\\n");
        return DigestUtils.md5Hex(pathvalue.getBytes(StandardCharsets.UTF_8));
    }
    /**
     * * JSON对象转为网址传参格式（按key的首字母从小到大排序）
     * @param jsonObject json对象
     * @return 返回json对象的字符串
     */
    public static String json2pathValue(JSONObject jsonObject)  {
        Map<String, String> jsonMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            jsonMap.put(entry.getKey(), entry.getValue().toString());
        }
        List<String> keyList = new ArrayList<>(jsonMap.keySet());
        List<String> collect = keyList.stream().sorted().collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : collect) {
            String value;
            if(jsonMap.get(s) == null){
                value = "";
            }else{
                value =jsonMap.get(s);
            }
            stringBuilder.append(s).append("=").append(value).append("&");
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("&"));
        return stringBuilder.toString();
    }
}
