package com.lyentech.lib.global.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

public class UploadBody extends RequestBody {
    private RequestBody requestBody;
    private ProgressListener progressListener;

    public UploadBody(RequestBody requestBody, ProgressListener listener) {
        this.requestBody = requestBody;
        this.progressListener = listener;
    }


    @Nullable
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(@NonNull BufferedSink bufferedSink) throws IOException {
        BufferedSink bufferedSink1 = Okio.buffer(new ForwardingSink(bufferedSink) {
            private long bytesWritten = 0L;
            private long contentLength = 0L;

            @Override
            public void write(@NonNull Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                progressListener.onProgress(bytesWritten * 100 / contentLength, contentLength);
            }
        });
        requestBody.writeTo(bufferedSink1);
        bufferedSink1.flush();
    }
}
