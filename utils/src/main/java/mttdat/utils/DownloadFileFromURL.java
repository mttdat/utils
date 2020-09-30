package mttdat.utils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class DownloadFileFromURL extends AsyncTask<String, Float, String> {

    private final int MAX_DOWNLOAD_FILE_SIZE = 8192;    // 8 MB.
    private final int BLOCK_WRITE_FILE_SIZE = 1024;    // 1 MB.
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";

    private int maxDownLoadFileSize;    // Unit: byte.
    private int blockWriteFileSize;     // Unit: byte.

    private HashMap<String, String> headers;   // Key - value in header.
    private String method;  // POST, GET, PUT, DELETE
    private HashMap<String, String> params;    // Key - value in param.

    public DownloadFileFromURL(int maxDownLoadFileSize, int blockWriteFileSize) {
        this.maxDownLoadFileSize = maxDownLoadFileSize;
        this.blockWriteFileSize = blockWriteFileSize;
        headers = new HashMap<>();
        params = new HashMap<>();
    }

    public DownloadFileFromURL() {
        headers = new HashMap<>();
        params = new HashMap<>();
    }

    public interface Listener {
        void onStart();
        void onUpdateProgress(float progress);
        void onFinish(String directorySavedFile);
        void onError(String message);
    }

    private Listener listener;

    /**
     * Before starting background thread Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(listener != null){
            listener.onStart();
        }
    }

    /* *
     * Downloading file in background thread
     *
     * @asParams[0]: url to download file.
     * @asParams[1]: Directory where to save downloaded file.
     * */
    @Override
    protected String doInBackground(String... asParams) {
        int count;
        try {

            URL url;

            if(this.params != null && this.params.size() > 0 && method.equals(METHOD_GET)){
                String query = getQuery(this.params);
                url = new URL(asParams[0] + "?" + query);
            }else {
                url = new URL(asParams[0]);
            }

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if(!TextUtils.isEmpty(method)){
                connection.setRequestMethod(method);
            }

            if(headers != null && headers.size() > 0){

                for (Map.Entry<String, String> header: headers.entrySet()) {
                    connection.addRequestProperty(header.getKey(), header.getValue());
                }
            }

            if(this.params != null && this.params.size() > 0 && method.equals(METHOD_POST)){

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8.name())
                );
                writer.write(getQuery(this.params));

                writer.flush();

                writer.close();
                os.close();
            }

            // Get length of file.
            int lengthOfFile = connection.getContentLength();

            // Download the file into input stream.
            InputStream input = new BufferedInputStream(connection.getInputStream(), maxDownLoadFileSize == 0 ? MAX_DOWNLOAD_FILE_SIZE : maxDownLoadFileSize);

            // Open output stream to write file.
            FileOutputStream fos = new FileOutputStream(asParams[1]);

            // Create an instance of block data.
            byte[] data = new byte[blockWriteFileSize == 0 ? BLOCK_WRITE_FILE_SIZE : blockWriteFileSize];
            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;

                // Update progress.
                publishProgress(total * 100f / lengthOfFile);

                // Writing data to file
                fos.write(data, 0, count);
            }

            // Flushing output.
            fos.flush();

            // Closing streams.
            fos.close();
            input.close();

        } catch (Exception e) {
            Log.e("DownloadFile AsyncTask", "Error: " + e.getMessage());
            asParams[1] =null;
            listener.onError(e.getMessage());
        }

        return asParams[1];
    }

    // Updating.
    @Override
    protected void onProgressUpdate(Float... progress) {
        if(listener != null){
            listener.onUpdateProgress(progress[0]);
        }
    }

    // Completing background task.
    @Override
    protected void onPostExecute(String directorySavedFile) {
        if(listener != null){
            listener.onFinish(directorySavedFile);
        }
    }

    private String getQuery(HashMap<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> param: params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(param.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public DownloadFileFromURL setMaxDownLoadFileSize(int maxDownLoadFileSize) {
        this.maxDownLoadFileSize = maxDownLoadFileSize;
        return this;
    }

    public DownloadFileFromURL setBlockWriteFileSize(int blockWriteFileSize) {
        this.blockWriteFileSize = blockWriteFileSize;
        return this;
    }

    // NOTICE: header must be set after method.
    public DownloadFileFromURL addHeader(String key, String value){
        headers.put(key, value);
        return this;
    }

    public DownloadFileFromURL setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    // NOTICE: method must be set first of all.
    public DownloadFileFromURL setMethod(String method) {
        this.method = method;

        switch (method){
            case METHOD_GET:

                headers.put("Accept-Charset", StandardCharsets.UTF_8.name());

                break;
            case METHOD_POST:

                headers.put("Accept-Charset", StandardCharsets.UTF_8.name());
                headers.put("Content-Type", "application/x-www-form-urlencoded;charset=" + StandardCharsets.UTF_8.name());

                break;
        }

        return this;
    }

    public DownloadFileFromURL setParams(HashMap<String, String> params) {
        this.params = params;
        return this;
    }

    public DownloadFileFromURL addParam(String key, String value){
        params.put(key, value);
        return this;
    }
}
