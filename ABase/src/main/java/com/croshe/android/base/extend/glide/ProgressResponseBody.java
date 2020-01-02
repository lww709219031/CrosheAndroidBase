package com.croshe.android.base.extend.glide;

import android.support.annotation.NonNull;

import com.croshe.android.base.listener.OnCrosheImageProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by sunfusheng on 2017/6/14.
 */
public class ProgressResponseBody extends ResponseBody {

    private String imageUrl;
    private ResponseBody responseBody;
    private OnCrosheImageProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(String url, ResponseBody responseBody, OnCrosheImageProgressListener progressListener) {
        this.imageUrl = url;
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0;

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += (bytesRead == -1) ? 0 : bytesRead;

                if (progressListener != null) {
                    progressListener.onProgress(imageUrl, totalBytesRead, contentLength(), totalBytesRead == contentLength(), null);
                }
                return bytesRead;
            }
        };
    }
}
