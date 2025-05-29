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

package top.nustar.nustarmythicmobsextension.myauth.web;

import com.alibaba.fastjson2.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.Getter;
import top.nustar.nustarmythicmobsextension.myauth.entity.Data;
import top.nustar.nustarmythicmobsextension.myauth.utils.Sign;

public class Update {
    public JSONObject jsonObject = new JSONObject();
    public JSONObject dataObject = new JSONObject();
    public Data data;

    @Getter
    private final JSONObject result;

    public Update() {
        data = new Data();
        this.dataObject.put("device_info", this.data.getDeviceInfo());
        this.dataObject.put("device_code", this.data.getDeviceCode());
        this.dataObject.put("timestamp", this.data.getTimestamp());
        this.jsonObject.put("data", dataObject);
        this.jsonObject.put("skey", "ff04bfa8-35d9-4a2c-b1b6-fcfc0d4663d3");
        this.jsonObject.put("vkey", "BF64F0CF-77CF-4A70-BB07-C00E4F59CFEB");
        this.jsonObject.put("sign", Sign.calculateSign(this.dataObject, "woshinidie666666"));
        result = JSONObject.parseObject(response().getBody(), JSONObject.class);
    }

    public HttpResponse<String> response() {
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
