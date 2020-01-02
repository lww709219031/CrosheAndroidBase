package com.croshe.android.base.entity;

import com.croshe.android.base.AIntent;
import com.croshe.android.base.server.BaseRequest;
import com.croshe.android.base.utils.NumberUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.googlecode.openbeans.BeanInfo;
import com.googlecode.openbeans.IntrospectionException;
import com.googlecode.openbeans.Introspector;
import com.googlecode.openbeans.PropertyDescriptor;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 所有实体类的基类
 * 安徽创息软件科技有限公司-技术支持，http://wwww.croshe.com
 * Created by Janesen on 2017/9/11 16:47.
 */
public class BaseEntity extends LinkedHashMap {

    private transient PropertyDescriptor[] propertyDescriptors;
    private transient long objId = System.currentTimeMillis();
    private transient String finalKey = "CROSHE";


    public <T> T fromSerializable(Serializable serializable) {
        if (serializable instanceof Map) {
            String jsonStr = new Gson().toJson(serializable);
            HashMap hashMap = new Gson().fromJson(jsonStr, HashMap.class);
            Iterator iterator = hashMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                put(key, hashMap.get(key));
            }
        }
        return (T) this;
    }


    public <T> T fromObject(Object object) {
        String jsonStr = new Gson().toJson(object);
        HashMap hashMap = new Gson().fromJson(jsonStr, HashMap.class);
        Iterator iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            put(key, hashMap.get(key));
        }
        return (T) this;
    }


    public <T> T fromJsonStr(String jsonStr) {
        HashMap hashMap = new Gson().fromJson(jsonStr, HashMap.class);
        Iterator iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            put(key, hashMap.get(key));
        }
        return (T) this;
    }


    public BaseEntity() {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
            propertyDescriptors = beanInfo.getPropertyDescriptors();
            put(finalKey, String.valueOf(System.currentTimeMillis()));
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }


    /**
     * 将自定义的属性填充到map中
     */
    private void fromProperty() {
        try {
            for (PropertyDescriptor property : propertyDescriptors) {
                if (property.getWriteMethod() == null || property.getReadMethod() == null) continue;
                String key = property.getName();
                Object value = property.getReadMethod().invoke(this);
                if (value != null) {
                    super.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置属性值
     *
     * @param attr
     * @param value
     */
    private boolean set(String attr, Object value) {
        try {
            for (PropertyDescriptor property : propertyDescriptors) {
                if (property.getWriteMethod() == null) continue;
                String key = property.getName();
                if (key.equals(attr)) {
                    Type[] params = property.getWriteMethod().getGenericParameterTypes();
                    if (params.length > 0) {
                        Object setValue = parseData(property, value, params[0]);
                        property.getWriteMethod().invoke(this, setValue);
                    } else {
                        property.getWriteMethod().invoke(this);
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.HashMap#entrySet()
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        // TODO Auto-generated method stub
        fromProperty();
        return super.entrySet();
    }


    @Override
    public Object put(Object key, Object value) {
        // TODO Auto-generated method stub
        if (super.containsKey(finalKey)) {
            super.remove(finalKey);
        }
        set(key.toString(), value);
        return super.put(key, value);
    }


    /**
     * 转换数据
     *
     * @param data
     * @return
     */
    private Object parseData(PropertyDescriptor property, Object data, Type type) {
        try {
            if (data != null) {
                if (type == String.class) {
                    return data.toString();
                } else if (type == Integer.class) {
                    return NumberUtils.formatToInt(data);
                } else if (type == Float.class) {
                    return NumberUtils.formatToFloat(data);
                } else if (type == Double.class) {
                    return NumberUtils.formatToDouble(data);
                } else if (type == Long.class) {
                    return NumberUtils.formatToLong(data);
                } else if (type == Boolean.class) {
                    return Boolean.parseBoolean(data.toString());
                }

                Gson gson = new GsonBuilder().serializeNulls().create();
                Object value = gson.fromJson(gson.toJson(data), type);
                return value;
            }
        } catch (JsonSyntaxException e) {
            AIntent.doAlert("JSON解析发生错误！" +
                    "\n【所在类：" + this.getClass() + "】" +
                    "\n【字段名：" + property.getName() + "】" +
                    "\n【字段类型：" + type + "】" +
                    "\n【JSON值：" + new Gson().toJson(data) + "】" +
                    "\n【错误信息：" + e.getLocalizedMessage() + "】");
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 转成json字符串
     *
     * @return
     */
    public String toJson() {
        return new Gson().toJson(this);
    }


    public String getServerMainUrl() {
        return BaseRequest.mainUrl;
    }


    public String getUrl(String attr) {
        if (containsKey(attr) && get(attr) != null) {
            String url = get(attr).toString();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = getServerMainUrl() + url;
            }
            return url;
        }
        return null;
    }

    public int getInt(String attr) {
        return NumberUtils.formatToInt(get(attr));
    }


    public int getInt(String attr, int defaultValue) {
        return NumberUtils.formatToInt(get(attr), defaultValue);
    }

    public double getDouble(String attr) {
        return NumberUtils.formatToDouble(get(attr));
    }


    public double getDouble(String attr, double defaultValue) {
        return NumberUtils.formatToDouble(get(attr), defaultValue);
    }

    public float getFloat(String attr) {
        return NumberUtils.formatToFloat(get(attr));
    }


    public float getFloat(String attr, float defaultValue) {
        return NumberUtils.formatToFloat(get(attr), defaultValue);
    }


    public long getLong(String attr) {
        return NumberUtils.formatToLong(get(attr));
    }


    public long getLong(String attr, long defaultValue) {
        return NumberUtils.formatToLong(get(attr), defaultValue);
    }


    public boolean getBoolean(String attr) {
        return Boolean.parseBoolean(get(attr).toString());
    }

    public boolean getBoolean(String attr, boolean defaultValue) {
        if (!containsKey(attr)) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(get(attr).toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public String getString(String attr) {
        return getString(attr, null);
    }

    public String getString(String attr, String defaultValue) {
        if (!containsKey(attr)) {
            return defaultValue;
        }
        try {
            return get(attr).toString();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获得实体对象的唯一标示ID
     *
     * @return
     */
    public long getObjId() {
        return objId;
    }
}
