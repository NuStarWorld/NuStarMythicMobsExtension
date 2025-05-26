package top.nustar.nustarmythicmobsextension.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import team.idealstate.sugar.logging.Log;
import top.nustar.nustarmythicmobsextension.myauth.web.Update;

public class UpdateUtil {

    public static void checkUpdate() {
        Update update = new Update();
        JSONObject updateResultData = update.getResult().getJSONObject("result");
        JSONArray verList = updateResultData.getJSONArray("list");
        if (updateResultData.get("haveNew").toString().equals("1")) {
            JSONObject newVerData = verList.getJSONObject(0);
            Log.info(() -> "检测到新版本,您使用的版本:" + updateResultData.get("ver").toString());
            Log.info("=============================");
            Log.info(() -> "最新版本: " + newVerData.get("ver").toString() + " 更新内容如下");
            for (String log : update.getUpdateLog(newVerData)) {
                Log.info(log);
            }
            Log.info("=============================");
        } else {
            Log.info("当前版本为最新版本");
        }
    }
}
