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
        this.deviceInfo = System.getProperty("os.name") + " " + System.getProperty("os.version");
        this.timestamp = String.valueOf(System.currentTimeMillis() / 1000);
    }
}
