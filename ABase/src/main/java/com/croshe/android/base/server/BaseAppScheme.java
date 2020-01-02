package com.croshe.android.base.server;

/**
 * Created by Janesen on 2017/4/22.
 */

public class BaseAppScheme {

    /**
     * 添加好友路径协议
     */
    public static final String addFriendScheme = "croshe://croshe.add.friend/";


    /**
     * 聊天群路径协议
     */
    public static final String joinChatGroupScheme = "croshe://croshe.join.chat.group/";





    /**
     * 构建添路径映射协议地址
     * @param targetValue
     * @return
     */
    public static String buildSchemeContent(String schemeHeader,String targetValue) {
        return schemeHeader + targetValue;
    }


    /**
     * 判断路径映射协议地址
     * @param schemeUrl
     * @return
     */
    public static boolean validateScheme(String schemeHeader,String schemeUrl) {
        if (schemeUrl.startsWith(schemeHeader)) {
            return true;
        }
        return false;
    }




    /**
     * 获得路径映射协议地址中的值
     * @param schemeUrl
     * @return
     */
    public static String getTargetValue(String schemeHeader,String schemeUrl) {
        String base64Value= schemeUrl.replace(schemeHeader, "");
        return base64Value;
    }


    /**
     * 获得路径映射协议地址中的值
     * @param schemeUrl
     * @return
     */
    public static String getTargetValueNormal(String schemeHeader,String schemeUrl) {
        String base64Value= schemeUrl.replace(schemeHeader, "");
        return base64Value;
    }

}
