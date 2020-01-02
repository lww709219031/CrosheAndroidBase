package com.croshe.android.base.extend.glide;

import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.GlideException;
import com.croshe.android.base.listener.OnCrosheImageProgressListener;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by sunfusheng on 2017/6/14.
 */
public class ProgressManager {

    private static List<WeakReference<OnCrosheImageProgressListener>> listeners = Collections.synchronizedList(new ArrayList<WeakReference<OnCrosheImageProgressListener>>());
    private static OkHttpClient okHttpClient;

    private ProgressManager() {
    }

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Request request = chain.request();
                            Response response = chain.proceed(request);
                            return response.newBuilder()
                                    .body(new ProgressResponseBody(request.url().toString(), response.body(), LISTENER))
                                    .build();
                        }
                    })
                    .build();
        }
        return okHttpClient;
    }

    private static final OnCrosheImageProgressListener LISTENER = new OnCrosheImageProgressListener() {
        @Override
        public void onProgress(String imageUrl, long bytesRead, long totalBytes, boolean isDone, GlideException exception) {
            if (listeners == null || listeners.size() == 0) return;
            listeners.removeAll(Collections.singleton(null));
            for (int i = 0; i < listeners.size(); i++) {
                WeakReference<OnCrosheImageProgressListener> listener = listeners.get(i);
                OnCrosheImageProgressListener progressListener = listener.get();
                if (progressListener!= null) {
                    progressListener.onProgress(imageUrl, bytesRead, totalBytes, isDone, exception);
                }
            }
        }
    };

    public static void addProgressListener(OnCrosheImageProgressListener progressListener) {
        if (progressListener == null) return;

        if (findProgressListener(progressListener) == null) {
            listeners.add(new WeakReference<>(progressListener));
        }
    }

    public static void removeProgressListener(OnCrosheImageProgressListener progressListener) {
        if (progressListener == null) return;

        WeakReference<OnCrosheImageProgressListener> listener = findProgressListener(progressListener);
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private static WeakReference<OnCrosheImageProgressListener> findProgressListener(OnCrosheImageProgressListener listener) {
        if (listener == null) return null;
        if (listeners == null || listeners.size() == 0) return null;

        for (int i = 0; i < listeners.size(); i++) {
            WeakReference<OnCrosheImageProgressListener> progressListener = listeners.get(i);
            if (progressListener.get() == listener) {
                return progressListener;
            }
        }
        return null;
    }
}
