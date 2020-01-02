package com.croshe.android.base.entity;

import com.alibaba.fastjson.JSON;
import com.croshe.android.base.utils.NumberUtils;
import com.croshe.android.base.utils.OKHttpUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.xw.Encrypt;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * HTTP 网络请求回调数据
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/7/9 13:21.
 */
public abstract class SimpleHttpCallBack<T> extends OKHttpUtils.CrosheHttpCallBack {

    protected boolean isCacheData = false;
    protected boolean flag = true;

    @Override
    public void onResult(boolean isCacheData, boolean success, String response) {
        try {
            this.isCacheData = isCacheData;
            if (success) {
                JSONObject jsonObject = new JSONObject(response);
                Object data = null;
                if (!jsonObject.isNull("data")) {
                    String decryptData = Encrypt.decrypt(jsonObject.getString("data"));
                    data = JSON.parse(decryptData);

                }
                Object otherData = null;
                if (!jsonObject.isNull("other")) {
                    otherData = jsonObject.get("other");
                }

                boolean requestSuccess = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");
                onCallBack(requestSuccess, message, data, otherData);

                Type genType = getClass().getGenericSuperclass();
                if (genType != null && genType instanceof ParameterizedType) {
                    T dataEntity = null;
                    Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                    if (params.length > 0) {
                        if (params[0] instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) params[0];
                            if (parameterizedType.getRawType() == MultiData.class) {
                                Type[] childTypeParams = parameterizedType.getActualTypeArguments();
                                if (childTypeParams.length > 1) {//2个参数，一般为MultiData
                                    MultiData multiHttpCallBack = new MultiData<>();
                                    multiHttpCallBack.setData(parseData(data, childTypeParams[0]));
                                    multiHttpCallBack.setOther(parseData(otherData, childTypeParams[1]));
                                    dataEntity = (T) multiHttpCallBack;
                                }
                            } else {
                                dataEntity = (T) parseData(data, params[0]);
                            }
                        } else {
                            dataEntity = (T) parseData(data, params[0]);
                        }
                    }
                    onCallBackEntity(requestSuccess, message, dataEntity);
                }
            } else {
                onCallBack(false, response, null);
                onCallBackEntity(false, response, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onCallBack(boolean success, String message, Object data) {

    }

    public void onCallBack(boolean success, String message, Object data, Object other) {
        onCallBack(success, message, data);
    }


    public void onCallBackEntity(boolean success, String message, T data) {

    }


    /**
     * 转换数据
     *
     * @param data
     * @return
     */
    private Object parseData(Object data, Type type) {
        try {
            if (data != null) {
                if (data instanceof String) {
                    return data.toString();
                } else if (data instanceof Integer) {
                    return NumberUtils.formatToInt(data);
                } else if (data instanceof Float) {
                    return NumberUtils.formatToFloat(data);
                } else if (data instanceof Double) {
                    return NumberUtils.formatToDouble(data);
                } else if (data instanceof Long) {
                    return NumberUtils.formatToLong(data);
                } else if (data instanceof Number) {
                    return NumberUtils.formatToNumber(data);
                }
                return new Gson().fromJson(data.toString(), type);
            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 取消请求
     */
    public void cancelRequest() {
        getOnCancelRequest().cancelRequest();
    }
}
