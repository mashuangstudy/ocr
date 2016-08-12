package com.hejinonline.chart.util;

/**
 * Created by zhangyayun on 16-6-20.
 */
public class UnicodeCharUtil {


    public static String charToUnicode(String strChar) {

        StringBuffer rtn = new StringBuffer();
        for (int i = 0; i < strChar.length(); i++) {
            String strHex =Integer.toHexString(strChar.charAt(i));
            for(int j=strHex.length() - 1;j<3;j++){
                rtn.append("0");
            }
            rtn.append(strHex);
        }
        return rtn.toString();

    }

    public static String unicodeToChar(String strNum) {

        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = strNum.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = strNum.substring(start + 2, strNum.length());
            } else {
                charStr = strNum.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();

    }

}
