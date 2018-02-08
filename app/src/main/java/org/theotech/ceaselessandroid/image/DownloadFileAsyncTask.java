package org.theotech.ceaselessandroid.image;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

import okio.BufferedSink;
import okio.Okio;

/**
 * Downloads the response of a url and saves it to a file
 * Right now it is used to save the scripture image
 * Eventually if it accepted a request instead of a url we could have it do more.
 * Created by kirisu on 10/24/15.
 * Some learnings from http://stackoverflow.com/questions/25893030/download-binary-file-from-okhttp
 * and http://stackoverflow.com/questions/30277583/saving-image-from-url-using-picasso-without-change-in-size-using-bitmap-compres
 */
public class DownloadFileAsyncTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = DownloadFileAsyncTask.class.getSimpleName();
    String url;
    File output;
    Context context;

    public DownloadFileAsyncTask(Context context, String url, File output) {
        this.context = context;
        this.url = url;
        this.output = output;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Request request = new Request.Builder().url(url).build();
        Response response;

        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            BufferedSink sink = Okio.buffer(Okio.sink(this.output));
            sink.writeAll(response.body().source());
            sink.close();
            Log.d(TAG, "downloaded file " + url + " to " + this.output);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "failed to download file " + url);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        // noop for now.
    }
}
