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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.serverct.ersha.attribute.data.AttributeData;

public class AttributeUtils {

    public static List<String> getWhiteAttributeList(AttributeData attributeData, List<String> configWhiteAttrList) {
        List<String> whiteAttrList = new ArrayList<>();
        for (String attrName : configWhiteAttrList) {
            whiteAttrList.add(attrName + ":" + attributeData.getAttributeValue(attrName)[0]);
        }
        return whiteAttrList;
    }

    public static Map<String, Integer> getPercentageAttr(List<String> percentageAttrList) {
        Map<String, Integer> percentageAttr = new HashMap<>();
        for (String attr : percentageAttrList) {
            String[] attrSplit;
            attrSplit = attr.split(":");
            if (attrSplit.length != 2) {
                attrSplit = attr.split("=");
            }
            percentageAttr.put(attrSplit[0], Integer.parseInt(attrSplit[1]));
        }
        return percentageAttr;
    }
}
