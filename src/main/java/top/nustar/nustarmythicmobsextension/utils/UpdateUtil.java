/*
 *    NuStarMythicMobsExtension
 *    Copyright (C) 2025  NuStar
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
            Log.info(String.format("Version checker: '%s'", "检测到新版本,您使用的版本:" + updateResultData.get("ver").toString()));
            Log.info(String.format("Version checker: '%s'", "============================="));
            Log.info(String.format("Version checker: '%s'", "最新版本: " + newVerData.get("ver").toString() + " 更新内容如下"));
            for (String log : update.getUpdateLog(newVerData)) {
                Log.info(String.format("Version checker: '%s'", log));
            }
            Log.info(String.format("Version checker: '%s'", "============================="));
        } else {
            Log.info(String.format("Version checker: '%s'", "当前版本为最新版本"));
        }
    }
}
