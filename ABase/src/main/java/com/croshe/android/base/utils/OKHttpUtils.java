package com.croshe.android.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.croshe.android.base.entity.HttpParams;
import com.croshe.android.base.entity.dao.AppCacheEntity;
import com.croshe.android.base.entity.dao.AppCacheHelper;
import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.zibin.luban.Luban;


/**
 * http请求工具类
 */
public class OKHttpUtils {

    public static String TAG = "OKHttp";
    private static final Map<String, String> headers = new HashMap<>();
    public static MediaType JSONMediaType = MediaType.parse("application/json;charset=utf-8");

    /**
     * 大城小家签名key
     */
    public static final String DCXJ_SIGNKEY = "dcxj_hd";


    /**
     * 签名的key
     */
    public static String SIGNKEY = null;

    /**
     * RSA解密公钥
     */
    public static String RSA_PUBLIC_KEY;

    /**
     * 是否加密请求
     */
    public static boolean ENCRYPT_REQUEST = false;


    /**
     * 是否缓存数据,默认为true
     */
    public static boolean CacheData = true;

    /**
     * 是否自动压缩图片
     */
    public static boolean CompressImage = false;


    /**
     * 用来配置全局默认的参数
     */
    private static final Map<String, Object> finalParams = new HashMap<>();

    /**
     * 添加全局头部
     *
     * @param key
     * @param value
     */
    public static void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * 移除全局头部
     *
     * @param key
     */
    public static void removeHeader(String key) {
        headers.remove(key);
    }


    /**
     * 添加全局参数
     *
     * @param key
     * @param value
     */
    public static void addFinalParams(String key, Object value) {
        finalParams.put(key, value);
    }


    /**
     * 移除全局参数
     *
     * @param key
     */
    public static void removeFinalParams(String key) {
        finalParams.remove(key);
    }


    /**
     * 获得全局参数
     *
     * @param key
     */
    public static Object getFinalParams(String key) {
        return finalParams.get(key);
    }


    private static OKHttpUtils okHttpUtils;

    /**
     * 可设置缓存
     *
     * @param context
     * @return
     */
    public static OKHttpUtils getInstance(Context context) {
        if (okHttpUtils == null) {
            okHttpUtils = new OKHttpUtils();
        }
        if (okHttpUtils.memoryDataInfo == null) {
            okHttpUtils.memoryDataInfo = context.getSharedPreferences("httpLocalData", 0);
        }
        return okHttpUtils;
    }

    /**
     * 不可设置缓存
     *
     * @return
     */
    public static OKHttpUtils getInstance() {
        if (okHttpUtils == null) {
            okHttpUtils = new OKHttpUtils();
        }
        return okHttpUtils;
    }


    private Handler mHandler;
    private OkHttpClient mOkHttpClient;
    public SharedPreferences memoryDataInfo;

    private OKHttpUtils() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public OkHttpClient getOkHttpClient() {
        return getOkHttpClient(1000 * 60);
    }

    public OkHttpClient getOkHttpClient(long timeout) {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .sslSocketFactory(createSSLSocketFactory())
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .cookieJar(new CookieJar() {
                        private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            cookieStore.put(url, cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = cookieStore.get(url);
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    }).build();


        }
        return mOkHttpClient;
    }

    /**
     * 执行post 异步请求
     *
     * @param url
     * @param params
     */
    public void post(final String url, final Map<String, Object> params, final HttpCallBack callback) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, false, false, false, false, true, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     * @param paramEntity
     */
    public void post(final String url, final Object paramEntity, final HttpCallBack callback) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return entityToMap(paramEntity);
            }
        }, callback, false, false, false, false, true, null);
    }


    /**
     * 执行post 异步请求json格式
     *
     * @param url
     * @param params
     */
    public void postJSON(final String url, final Map<String, Object> params, final HttpCallBack callback) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, false, false, false, false, true, JSONMediaType);
    }


    /**
     * 执行post 异步请求json格式
     *
     * @param url
     * @param paramEntity
     */
    public void postJSON(final String url, final Object paramEntity, final HttpCallBack callback) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return entityToMap(paramEntity);
            }
        }, callback, false, false, false, false, true, JSONMediaType);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     * @param params
     */
    public void post(final String url, final Map<String, Object> params, final HttpCallBack callback, boolean isCache) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, isCache, false, false, false, true, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     * @param paramEntity
     */
    public void post(final String url, final Object paramEntity, final HttpCallBack callback, boolean isCache) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return entityToMap(paramEntity);
            }

        }, callback, isCache, false, false, false, true, null);
    }

    /**
     * 执行post 异步请求json格式
     *
     * @param url
     * @param params
     */
    public void postJSON(final String url, final Map<String, Object> params, final HttpCallBack callback, boolean isCache) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, isCache, false, false, false, true, JSONMediaType);
    }


    /**
     * 执行post 异步请求json格式
     *
     * @param url
     * @param paramEntity
     */
    public void postJSON(final String url, final Object paramEntity, final HttpCallBack callback, boolean isCache) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return entityToMap(paramEntity);
            }

        }, callback, isCache, false, false, false, true, JSONMediaType);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     * @param params
     */
    public void post(final String url, final Map<String, Object> params, final HttpCallBack callback,
                     boolean isCache, final boolean isDoRefresh) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, isCache, isDoRefresh, false, false, true, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     * @param paramEntity
     */
    public void post(final String url, final Object paramEntity, final HttpCallBack callback,
                     boolean isCache, final boolean isDoRefresh) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return entityToMap(paramEntity);
            }
        }, callback, isCache, isDoRefresh, false, false, true, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     * @param params
     */
    public void post(final String url, final Map<String, Object> params, final HttpCallBack callback,
                     boolean isCache, final boolean isDoRefresh, final boolean isRefreshCache) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, isCache, isDoRefresh, false, false, isRefreshCache, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     * @param paramEntity
     */
    public void post(final String url, final Object paramEntity, final HttpCallBack callback,
                     boolean isCache, final boolean isDoRefresh, final boolean isRefreshCache) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return entityToMap(paramEntity);
            }
        }, callback, isCache, isDoRefresh, false, false, isRefreshCache, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     */
    public void post(final String url, final AsyncMakeParams asyncMakeParams, final HttpCallBack callback) {
        doRequest(url, asyncMakeParams, callback, false, false, false, false, true, null);
    }


    /**
     * 执行post 异步请求 json格式
     *
     * @param url
     */
    public void postJSON(final String url, final AsyncMakeParams asyncMakeParams, final HttpCallBack callback) {
        doRequest(url, asyncMakeParams, callback, false, false, false, false, true, JSONMediaType);
    }

    /**
     * 执行post 异步请求
     *
     * @param url
     * @param params
     */
    public void postJSON(final String url, final Map<String, Object> params, final HttpCallBack callback, boolean isCache, final boolean isDoRefresh) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, isCache, isDoRefresh, false, false, true, JSONMediaType);
    }

    /**
     * 执行post 异步请求
     *
     * @param url
     * @param paramEntity
     */
    public void postJSON(final String url, final Object paramEntity, final HttpCallBack callback, boolean isCache, final boolean isDoRefresh) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return entityToMap(paramEntity);
            }
        }, callback, isCache, isDoRefresh, false, false, true, JSONMediaType);
    }


    /**
     * 执行get 异步请求
     *
     * @param url
     * @param params 自动转换成get参数拼接模式
     */
    public void get(final String url, final Map<String, Object> params, final HttpCallBack callback) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return params;
            }
        }, callback, false, false, true, false, true, null);
    }


    /**
     * 执行get 异步请求
     *
     * @param url
     * @param paramEntity 自动转换成get参数拼接模式
     */
    public void get(final String url, final Object paramEntity, final HttpCallBack callback) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return entityToMap(paramEntity);
            }
        }, callback, false, false, true, false, true, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     */
    public void get(final String url, final HttpCallBack callback) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return null;
            }
        }, callback, false, false, true, false, true, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     */
    public void get(final String url, final HttpCallBack callback, boolean isSynchronize) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return null;
            }
        }, callback, false, false, true, isSynchronize, true, null);
    }


    /**
     * 执行post 异步请求
     *
     * @param url
     */
    public void get(final String url, final HttpCallBack callback, boolean isCache, boolean isDoRefresh) {
        doRequest(url, new AsyncMakeParams() {

            @Override
            public Map<String, Object> doCreateParams() {
                // TODO Auto-generated method stub
                return null;
            }
        }, callback, isCache, isDoRefresh, true, false, true, null);
    }

    /**
     * 执行post 异步请求
     *
     * @param url
     */
    public void doRequest(final String url,
                          final AsyncMakeParams asyncMakeParams,
                          final HttpCallBack callback,
                          final boolean isCache,
                          final boolean isDoRefresh,
                          final boolean isGet,
                          boolean isSynchronize,
                          final boolean isRefreshCache,
                          MediaType mediaType) {
        if (callback == null) {
            throw new RuntimeException("Callback must not null.");
        }
        if (mediaType == null) {//默认为表单格式
            mediaType = MediaType.parse("application/x-www-form-urlencoded");
        }

        if (ENCRYPT_REQUEST) {
            RsaEncryptUtil.RSA_PUBLIC_KEY = OKHttpUtils.RSA_PUBLIC_KEY;
            if (StringUtils.isEmpty(RsaEncryptUtil.RSA_PUBLIC_KEY)) {
                throw new RuntimeException("解密或加密的公钥不可为空！OKHttpUtils.RSA_PUBLIC_KEY");
            }
        }


        final MediaType finalMediaType = mediaType;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    Map<String, Object> params = asyncMakeParams.doCreateParams();
                    if (params == null) params = new HashMap<>();

                    for (String key :
                            finalParams.keySet()) {
                        if (!params.containsKey(key)) {
                            params.put(key, finalParams.get(key));
                        }
                    }

                    if (CompressImage) {
                        Set<String> keys = params.keySet();
                        for (String key : keys) {
                            if (params.get(key) instanceof File) {
                                File file = (File) params.get(key);
                                if (FileUtils.isStaticImageUrl(file.getAbsolutePath())) {//如果是图片，则进行压缩
                                    try {
                                        List<File> files = Luban.with(ExitApplication.getContext().getApplicationContext())
                                                .load(file.getAbsolutePath())
                                                .ignoreBy(50)
                                                .setTargetDir(BaseAppUtils.getImageCacheDir(ExitApplication.getContext().getApplicationContext()))
                                                .get();
                                        if (files.size() > 0) {
                                            params.put(key, files.get(0));
                                        }
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }


                    Request.Builder request;
                    //缓存数据key
                    final String cacheKey = MD5Encrypt.MD5(url + params.toString());
                    if (callback instanceof CrosheHttpCallBack) {
                        ((CrosheHttpCallBack) callback).setOnCancelRequest(new CrosheHttpCallBack.OnCancelRequest() {
                            @Override
                            public void cancelRequest() {
                                cancel(cacheKey);
                            }
                        });
                        if (((CrosheHttpCallBack) callback).getOnStartRequest() != null) {
                            ((CrosheHttpCallBack) callback).getOnStartRequest().onStartRequest();
                        }
                    }


                    final AppCacheEntity appCacheEntity = AppCacheHelper.getCache(cacheKey);
                    final boolean[] isCallBacked = {false};
                    if (isCache && CacheData) {
                        if (appCacheEntity != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    isCallBacked[0] = true;
                                    if (callback instanceof CrosheHttpCallBack) {
                                        ((CrosheHttpCallBack) callback).onResult(true, true, appCacheEntity.getCacheContent());
                                    } else {
                                        callback.onResult(true, appCacheEntity.getCacheContent());
                                    }
                                }
                            });
                            if (!isRefreshCache) {
                                return;
                            }
                        }
                    }

                    //请求签名
                    requestSign(params, url);


                    if (isGet) {
                        if (params.size() > 0) {
                            if (url.contains("?")) {
                                request = new Request.Builder()
                                        .url(url + "&" + convertToGet(params))
                                        .tag(cacheKey);
                            } else {
                                request = new Request.Builder()
                                        .url(url + "?" + convertToGet(params))
                                        .tag(cacheKey);
                            }

                        } else {
                            request = new Request.Builder()
                                    .url(url)
                                    .tag(cacheKey);
                        }
                    } else {
                        if (params.size() > 0) {
                            Map<String, Object> normalParams = new HashMap<>();
                            if (finalMediaType == JSONMediaType) {//判断提交表单模式
                                request = new Request.Builder()
                                        .url(url)
                                        .tag(cacheKey)
                                        .post(RequestBody.create(JSONMediaType, JSON.toJSONString(params)));
                            } else {
                                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                                FormBody.Builder formEncodingBuilder = new FormBody.Builder();//上传参数保存对象key-value模式
                                Set<String> keys = params.keySet();

                                boolean haveFile = false;
                                for (String key : keys) {
                                    Object value = params.get(key);
                                    if (value == null) continue;
                                    if (value instanceof File) {
                                        File file = (File) value;
                                        if (file.exists()) {
                                            multipartBuilder.addFormDataPart(key, file.getName(),
                                                    RequestBody.create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));
                                            haveFile = true;
                                        }
                                    } else if (value instanceof File[]) {
                                        File[] files = (File[]) value;
                                        for (File file : files) {
                                            if (file.exists()) {
                                                multipartBuilder.addFormDataPart(key, file.getName(),
                                                        RequestBody.create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));
                                                haveFile = true;
                                            }
                                        }
                                    } else {
                                        if (ENCRYPT_REQUEST) {//非加密是追加参数
                                            normalParams.put(key, value);
                                        } else {
                                            multipartBuilder.addFormDataPart(key, value.toString());
                                            formEncodingBuilder.add(key, value.toString());
                                        }
                                    }
                                }


                                if (haveFile) {//如果有附件必须使用 multipart/form-data
                                    if (ENCRYPT_REQUEST) {
                                        String encryptData = RsaEncryptUtil.encryptByPublicKey(new Gson().toJson(normalParams));
                                        multipartBuilder.addFormDataPart("data", encryptData);
                                        request = new Request.Builder()
                                                .url(url)
                                                .tag(cacheKey)
                                                .addHeader("Encrypt", "true")
                                                .post(multipartBuilder.build());
                                    } else {
                                        request = new Request.Builder()
                                                .url(url)
                                                .tag(cacheKey)
                                                .post(multipartBuilder.build());
                                    }

                                } else {
                                    if (ENCRYPT_REQUEST) {
                                        String encryptData = RsaEncryptUtil.encryptByPublicKey(new Gson().toJson(normalParams));
                                        formEncodingBuilder.add("data", encryptData);
                                        request = new Request.Builder()
                                                .url(url)
                                                .addHeader("Encrypt", "true")
                                                .tag(cacheKey)
                                                .post(formEncodingBuilder.build());
                                    } else {
                                        request = new Request.Builder()
                                                .url(url)
                                                .tag(cacheKey)
                                                .post(formEncodingBuilder.build());
                                    }

                                }
                            }
                        } else {
                            request = new Request.Builder()
                                    .url(url)
                                    .tag(cacheKey);
                        }
                    }

                    //添加请求头
                    Set<String> headerKeys = headers.keySet();
                    for (String string : headerKeys) {
                        request.addHeader(string, headers.get(string));
                    }

                    Log.d(TAG, "请求类型：" + (isGet ? "GET" : "POST"));
                    Log.d(TAG, "提交类型：" + finalMediaType.toString());
                    Log.d(TAG, "请求地址：" + url);
                    Log.d(TAG, "请求参数：" + params.toString());
                    Log.d(TAG, "==================================================");


                    Call call = getOkHttpClient().newCall(request.build());
                    response = call.execute();
                    String html = response.body().string();

                    //需要进行rsa解密
                    String encrypt = response.header("Encrypt");
                    if (StringUtils.isNotEmpty(encrypt) && encrypt.equals("true")) {
                        RsaEncryptUtil.RSA_PUBLIC_KEY = OKHttpUtils.RSA_PUBLIC_KEY;
                        if (StringUtils.isEmpty(RsaEncryptUtil.RSA_PUBLIC_KEY)) {
                            throw new RuntimeException("解密的公钥不可为空！OKHttpUtils.RSA_PUBLIC_KEY");
                        }
                        html = RsaEncryptUtil.decryptByPublicKey(html);
                    }

                    response.body().close();
                    if (response.isSuccessful()) {
                        final String finalHtml = html;
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (isCache && CacheData) {
                                    AppCacheHelper.setCache(url, cacheKey, finalHtml);

                                    if (appCacheEntity == null || !isCallBacked[0]) {
                                        callback.onResult(true, finalHtml);
                                    } else {
                                        if (isDoRefresh) {
                                            callback.onResult(true, finalHtml);
                                        }
                                    }
                                } else {
                                    callback.onResult(true, finalHtml);
                                }
                            }
                        });
                    } else {
                        final String errorMessage = response.message();
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                callback.onResult(false, "请求失败，请稍后重试！" + errorMessage);
                            }
                        });
                    }
                } catch (final Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            if (e instanceof ConnectException) {
                                callback.onResult(false, "请求断开，请检查您的网络连接是否正常！" + e.toString());
                            } else if (e instanceof SocketTimeoutException || e instanceof SocketException) {
                                callback.onResult(false, "请求超时，请稍后重试！" + e.toString());
                            } else if (e instanceof IOException) {
                                if (StringUtils.isNotEmpty(e.getMessage()) && e.getMessage().equals("Canceled")) {
                                    callback.onResult(false, "请求已被取消！");
                                } else {
                                    callback.onResult(false, "请求失败，发生未知错误！" + e.toString());
                                }
                            } else {
                                callback.onResult(false, "请求失败，发生未知错误！" + e.toString());
                            }
                        }
                    });
                } finally {
                    try {
                        if (response != null) {
                            response.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        if (isSynchronize) {
            runnable.run();
        } else {
            new Thread(runnable).start();
        }
    }


    /**
     * 请求签名
     *
     * @param params
     * @param url
     * @throws Exception
     */
    public void requestSign(Map<String, Object> params, String url) throws Exception {
        params.put("timestamp", System.currentTimeMillis());
//        String sign = sign(params, url);

        //大城小家装修重新签名方法
        String sign = generateSignature(params, DCXJ_SIGNKEY);
        if (StringUtils.isNotEmpty(SIGNKEY)) {
            Random random = new Random();
            int crc = Math.max(1, random.nextInt(9));
            for (int i = 0; i < crc; i++) {
                sign = MD5Encrypt.MD5(sign + SIGNKEY + i);
            }
            params.put("crc", crc);
        }
        params.put("sign", sign);
    }


    /**
     * 下载文件支持断点下载
     *
     * @param url
     * @param callback
     */
    public void downFile(Context context, String url, final HttpDownFileCallBack callback) {
        downFile(context, url, null, callback);
    }

    /**
     * 下载文件支持断点下载
     *
     * @param url
     * @param callback
     */
    public void downFile(Context context, String url, String localPath, final HttpDownFileCallBack callback) {
        Call call;
        try {
            final SharedPreferences memoryDataInfo = context.getSharedPreferences("httpDownLocalData", 0);

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                final File doneFile = new File(url.replace("file://", ""));
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onDownLoad(doneFile.length(), doneFile.length(), doneFile.getPath());
                        }
                    }
                });
                return;
            }

            if (localPath == null) {
                localPath = context.getFilesDir() + "/DownFiles/" + MD5Encrypt.MD5(url);
            }

            final String cacheKey = MD5Encrypt.MD5(url + localPath);

            final File doneFile = new File(localPath);
            if (doneFile.exists()) {//如果存在已经下载完成的文件
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onDownLoad(doneFile.length(), doneFile.length(), doneFile.getPath());
                        }
                    }
                });
                return;
            }

            final File tempFile = new File(localPath + "temp");
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }

            cancel(cacheKey);//取消之前的请求

            Request.Builder builder = new Request.Builder().tag(cacheKey).url(url);

            Set<String> headerKeys = headers.keySet();
            for (String string : headerKeys) {
                builder.addHeader(string, headers.get(string));
            }
            //断点下载
            builder.addHeader("RANGE", "bytes=" + String.valueOf(tempFile.length()) + "-");
            call = getOkHttpClient().newCall(builder.build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    if (e instanceof ConnectException) {
                        callback.onDownFail("请求断开，请检查您的网络连接是否正常！");
                    } else if (e instanceof SocketTimeoutException || e instanceof SocketException) {
                        callback.onDownFail("请求超时，请稍后重试！");
                    } else {
                        callback.onDownFail("请求失败，发生未知错误！");
                    }
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    try {
                        final long tempLength = tempFile.length();
                        final long countLength = response.body().contentLength() + tempLength;

                        SharedPreferences.Editor editor = memoryDataInfo.edit();
                        editor.putLong(cacheKey, countLength);
                        editor.commit();

                        RandomAccessFile randomAccessFile = new RandomAccessFile(tempFile, "rwd");
                        randomAccessFile.seek(tempLength);
                        InputStream inputStream = response.body().byteStream();
                        byte[] buffer = new byte[512];
                        int readLength;
                        while ((readLength = inputStream.read(buffer)) != -1) {
                            randomAccessFile.write(buffer, 0, readLength);
                            //下载完毕
                            if (randomAccessFile.length() >= countLength) {
                                tempFile.renameTo(doneFile);
                                doneFile.setWritable(true);
                                doneFile.setReadable(true);
                                doneFile.setExecutable(true);
                            }
                            //是否继续下载
                            if (callback != null && !callback.onDownLoad(countLength, randomAccessFile.length(), doneFile.getPath())) {
                                break;
                            }
                        }
                        if (countLength == -1) {
                            callback.onDownLoad(randomAccessFile.length(), randomAccessFile.length(), doneFile.getPath());
                        }
                        randomAccessFile.close();
                        IOUtils.closeQuietly(inputStream);
                    } catch (Exception e) {
                        callback.onDownFail("文件下载失败：" + e.getMessage());
                    } finally {
                        if (response != null) {
                            response.close();
                        }
                    }
                }
            });
        } catch (Exception e) {
            callback.onDownFail("文件下载失败：" + e.getMessage());
        }
    }


    /**
     * 检测文件是否下载中
     *
     * @param url
     * @param localPath
     */
    public boolean exitsDownFile(String url, String localPath) {
        final String cacheKey = MD5Encrypt.MD5(url + localPath);
        return exist(cacheKey);
    }


    /**
     * 检测下载文件的信息
     *
     * @param context
     * @param url
     * @param localPath
     */
    public double checkDownFile(Context context, String url, String localPath) {
        return checkDownFile(context, url, localPath, null);
    }

    /**
     * 检测下载文件的信息
     *
     * @param context
     * @param url
     * @param localPath
     * @param callback
     */
    public double checkDownFile(Context context, String url, String localPath, final HttpDownFileCallBack callback) {
        final SharedPreferences memoryDataInfo = context.getSharedPreferences("httpDownLocalData", 0);

        final String cacheKey = MD5Encrypt.MD5(url + localPath);
        if (localPath == null) {
            localPath = context.getFilesDir() + "/DownFiles/" + MD5Encrypt.MD5(url);
        }
        final long countLength = memoryDataInfo.getLong(cacheKey, 0);

        final File doneFile = new File(localPath);
        if (doneFile.exists()) {//如果存在已经下载完成的文件
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onDownLoad(doneFile.length(), doneFile.length(), doneFile.getPath());
                    }
                }
            });
            double progress = Double.valueOf(doneFile.length()) / doneFile.length();
            return progress;
        } else {
            final File tempFile = new File(localPath + "temp");
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onDownLoad(countLength, tempFile.length(), doneFile.getPath());
                    }
                }
            });
            if (countLength == 0) {
                return 0;
            }
            double progress = Double.valueOf(tempFile.length()) / countLength;
            return progress;
        }
    }


    /**
     * 删除本地文件
     *
     * @param context
     * @param url
     * @param localPath
     */
    public void deleteFile(Context context, String url, String localPath) {

        if (localPath == null) {
            localPath = context.getFilesDir() + "/DownFiles/" + MD5Encrypt.MD5(url);
        }

        File tempFile = new File(localPath + "temp");
        tempFile.delete();

        File localFile = new File(localPath);
        localFile.delete();
    }

    /**
     * 删除指定缓存
     */
    public void deleteCache(String url, Map<String, Object> params) {
        for (String key :
                finalParams.keySet()) {
            if (!params.containsKey(key)) {
                params.put(key, finalParams.get(key));
            }
        }
        String cacheKey = MD5Encrypt.MD5(url + params.toString());
        AppCacheHelper.deleteCache(cacheKey);
    }

    /**
     * 删除指定路径下的缓存
     *
     * @param url
     */
    public void deleteCache(String url) {
        AppCacheHelper.deleteCacheByTag(url);
    }


    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 将参数转换成get形式
     *
     * @param params
     * @return
     */
    public String convertToGet(Map<String, Object> params) {
        List<String> list = new ArrayList<>();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            list.add(key + "=" + params.get(key));
        }
        return StringUtils.join(list, "&");
    }


    /**
     * 取消请求
     *
     * @param tag
     */
    public void cancel(String tag) {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            if (call.request().tag().equals(tag))
                call.cancel();
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            if (call.request().tag().equals(tag))
                call.cancel();
        }
    }


    /**
     * 判断请求是否存在
     *
     * @param tag
     */
    public boolean exist(String tag) {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            if (call.request().tag().equals(tag))
                return true;
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            if (call.request().tag().equals(tag))
                return true;
        }
        return false;
    }


    /**
     * 判断路径是否有效，不可在UI线程中运行
     *
     * @param url
     * @return
     */
    public boolean ping(String url) {
        try {
            Request request = new Request.Builder()
                    .head()
                    .url(url)
                    .build();
            Call requestCall = getOkHttpClient().newCall(request);
            Response response = requestCall.execute();
            return response.isSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 签名
     *
     * @param params
     * @return
     */
    public static String sign(Map<String, Object> params, String key) {
        StringBuilder builder = new StringBuilder();
        TreeSet<String> paramKeys = new TreeSet(params.keySet());
        for (String string : paramKeys) {
            if (params.get(string) != null
                    && StringUtils.isNotEmpty(params.get(string).toString())
                    && !(params.get(string) instanceof File)
                    && !(params.get(string) instanceof File[])) {
                builder.append(params.get(string));
            }
        }
        builder.append(key);//密钥 key
        return MD5Encrypt.MD5(builder.toString());
    }

    /**
     * 大城小家装修重新签名方法，使用的是微信的签名规则
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String generateSignature(Map<String, Object> data, String key) throws Exception {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        String[] var6 = keyArray;
        int var7 = keyArray.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            String k = var6[var8];
            if (!k.equals("sign")
                    && (data.get(k) != null)
                    && (StringUtils.isNotEmpty(String.valueOf(data.get(k))))
                    && !(data.get(k) instanceof File)
                    && !(data.get(k) instanceof File[])) {
                sb.append(k).append("=").append((data.get(k)).toString().trim()).append("&");
            }
        }

        sb.append("key=").append(key);
        return MD5Encrypt.MD5(sb.toString()).toUpperCase();
    }


    public static Map<String, Object> getUrlParams(String param) {
        Map<String, Object> map = new HashMap<>(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] urlParam = param.split("\\?");

        String[] params = urlParam[urlParam.length - 1].split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }


    /**
     * 获得Url的host
     *
     * @param url
     * @return
     */
    public static String getUrlHost(String url) {
        if (!(StringUtils.startsWithIgnoreCase(url, "http://") || StringUtils
                .startsWithIgnoreCase(url, "https://"))) {
            url = "http://" + url;
        }

        String returnVal = StringUtils.EMPTY;
        try {
            Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
            Matcher m = p.matcher(url);
            if (m.find()) {
                returnVal = m.group();
            }

        } catch (Exception e) {
        }
        if ((StringUtils.endsWithIgnoreCase(returnVal, ".html") || StringUtils
                .endsWithIgnoreCase(returnVal, ".htm"))) {
            returnVal = StringUtils.EMPTY;
        }
        return returnVal;
    }


    /**
     * 讲实体对象转换成map集合
     *
     * @param entity
     * @return
     */
    public static Map<String, Object> entityToMap(Object entity) {
        if (entity instanceof HttpParams) {
            return ((HttpParams) entity).toMap();
        }
        String jsonData = new Gson().toJson(entity);

        Map<String, Object> map = new Gson().fromJson(jsonData, new TypeToken<Map<String, Object>>() {
        }.getType());
        return map;
    }

    public interface HttpCallBack {
        void onResult(boolean success, String response);
    }

    public static abstract class CrosheHttpCallBack implements HttpCallBack {

        private OnCancelRequest onCancelRequest;
        private OnStartRequest onStartRequest;

        @Override
        public void onResult(boolean success, String response) {
            onResult(false, success, response);
        }

        public void onResult(boolean isCacheData, boolean success, String response) {

        }

        public OnCancelRequest getOnCancelRequest() {
            if (onCancelRequest == null) {
                onCancelRequest = new OnCancelRequest() {
                    @Override
                    public void cancelRequest() {

                    }
                };
            }
            return onCancelRequest;
        }

        void setOnCancelRequest(OnCancelRequest onCancelRequest) {
            this.onCancelRequest = onCancelRequest;
        }

        public OnStartRequest getOnStartRequest() {
            return onStartRequest;
        }

        public void setOnStartRequest(OnStartRequest onStartRequest) {
            this.onStartRequest = onStartRequest;
        }

        public interface OnCancelRequest {
            void cancelRequest();
        }

        public interface OnStartRequest {
            void onStartRequest();
        }

    }


    public interface HttpDownFileCallBack {
        boolean onDownLoad(long countLength, long downLength, String localPath);

        void onDownFail(String message);
    }

    /**
     * 异步生成参数,适用于文件处理，在主线程的话，会有卡顿现象
     *
     * @author Janesen
     */
    public interface AsyncMakeParams {
        Map<String, Object> doCreateParams();
    }

    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }


    /**
     * 获得文件名称
     *
     * @param urlString
     * @return
     */
    public static String getFileName(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == 200) {
                String contentDisposition = conn.getHeaderField("Content-Disposition");
                if (StringUtils.isNotEmpty(contentDisposition)) {
                    String reg = ".*filename=(.*)";
                    Matcher m = Pattern.compile(reg).matcher(contentDisposition);
                    if (m.find()) {
                        String urlName = URLDecoder.decode(m.group(1), "utf-8");
                        if (ChineseHelper.containsChinese(urlName)) {
                            return urlName;
                        } else {
                            return new String(urlName.getBytes("ISO-8859-1"), "gbk");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

