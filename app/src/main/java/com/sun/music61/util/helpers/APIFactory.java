package com.sun.music61.util.helpers;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.sun.music61.data.model.Response;
import com.sun.music61.util.CommonUtils;
import com.sun.music61.util.listener.APICallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This provides methods to help connect api working with AsyncTask.
 */
public class APIFactory extends AsyncTask<Void, Void, Response> {

    private static final String TAG = APIFactory.class.getName();
    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECTION_TIMEOUT = 5000;

    public interface Method {
        String GET = "GET";
        String POST = "POST";
    }

    private String mUrl;
    private String mMethod;
    private List<HashMap<String, String>> mAttributes;
    private APICallback mAPICallback;

    public static Builder Builder() {
        return new Builder();
    }

    public APIFactory(final Builder builder) {
        mUrl = builder.getUrl();
        mMethod = builder.getMethod();
        mAttributes = builder.getAttributes();
        mAPICallback = builder.getCallback();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Response doInBackground(Void... voids) {
        try {
            StringBuffer result = null;
            URL urlParse = new URL(mUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlParse.openConnection();
            httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            httpURLConnection.setReadTimeout(READ_TIMEOUT);
            switch (mMethod) {
                case Method.GET:
                    httpURLConnection.setRequestMethod(Method.GET);
                    result = fetchData(httpURLConnection);
                    break;
                case Method.POST:
                    httpURLConnection.setRequestMethod(Method.POST);
                    result = configPostMethod(httpURLConnection);
                    break;
            }

            return new Response(httpURLConnection.getResponseCode(), result);
        } catch (MalformedURLException e) {
            mAPICallback.onFailure(e);
        } catch (IOException e) {
            mAPICallback.onFailure(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        if (response != null)
            mAPICallback.onResponse(response);
        else
            mAPICallback.onFailure(new NullPointerException("Response is null"));
    }

    private StringBuffer fetchData(HttpURLConnection httpURLConnection) {
        InputStream inputStream;
        try {
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer data = new StringBuffer();

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                data.append(line);
            }

            // Free memory
            bufferedReader.close();
            reader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return data;

        } catch (MalformedURLException e) {
            Log.e(TAG, "fetchData: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "fetchData: " + e.getMessage());
        }
        return null;
    }

    private StringBuffer configPostMethod(HttpURLConnection httpURLConnection) {
        try {
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder();

            for (int i = 0; i < mAttributes.size(); i++) {
                String key = null;
                String value = null;
                for (Map.Entry<String, String> values : mAttributes.get(i).entrySet()) {
                    key = values.getKey();
                    value = values.getValue();
                }
                builder.appendQueryParameter(key, value);
            }

            String query = builder.build().getEncodedQuery();
            OutputStream outputStream = httpURLConnection.getOutputStream();
            OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter writer = new BufferedWriter(streamWriter);

            writer.write(query);
            writer.flush();

            // Free memory
            writer.close();
            streamWriter.close();
            outputStream.close();

            return fetchData(httpURLConnection);

        } catch (ProtocolException e) {
            Log.e(TAG, "configPostMethod: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "configPostMethod: " + e.getMessage());
        }
        return null;
    }


    public static class Builder {
        private String mUrl;
        private APICallback mCallback;
        private String mMethod;
        List<HashMap<String, String>> mAttributes;

        public Builder baseUrl(String url) {
            mUrl = url;
            return this;
        }

        public Builder method(String method) {
            mMethod = method;
            return this;
        }

        public Builder attribute(List<HashMap<String, String>> attributes) {
            mAttributes = attributes;
            return this;
        }

        public Builder enqueue(APICallback callback) {
            mCallback = callback;
            return this;
        }

        public void build() {
            mUrl = CommonUtils.checkNotNull(mUrl) ? mUrl : Method.GET;
            mMethod = CommonUtils.checkNotNull(mMethod) ? mMethod : Method.GET;
            new APIFactory(this).execute();
        }

        private String getUrl() {
            return mUrl;
        }

        private APICallback getCallback() {
            return mCallback;
        }

        private String getMethod() {
            return mMethod;
        }

        private List<HashMap<String, String>> getAttributes() {
            return mAttributes;
        }
    }
}
