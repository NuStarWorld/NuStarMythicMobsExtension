package top.nustar.nustarmythicmobsextension.utils;

import org.serverct.ersha.attribute.data.AttributeData;

import java.util.ArrayList;
import java.util.List;

public class AttributeUtils {

    public static List<String> getWhiteAttributeList(AttributeData attributeData, List<String> configWhiteAttrList) {
        List<String> whiteAttrList = new ArrayList<>();
        for (String attrName : configWhiteAttrList) {
            whiteAttrList.add(attrName + ":" + attributeData.getAttributeValue(attrName)[0]);
        }
        return whiteAttrList;
    }
}
