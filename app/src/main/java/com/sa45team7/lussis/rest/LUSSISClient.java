package com.sa45team7.lussis.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nhatton on 1/17/18.
 * Service class to make http request
 */

public class LUSSISClient {

//    private static String ROOT_URL = "https://10.211.55.5/LUSSIS/api/";
    private static String ROOT_URL = "https://10.0.2.2/LUSSIS/api/";
//    private static String ROOT_URL = "https://27d06691.ap.ngrok.io/LUSSIS/api/";

    private static volatile Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            synchronized (LUSSISClient.class) {
                if (retrofit == null) {
                    retrofit = getRetrofitObj();
                }
            }
        }

        return retrofit;
    }

    private static Retrofit getRetrofitObj() {
        try {
            //Remove the ssl verification on the phone side
            // Create a trust manager that does not validate certificate chains
            final X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            };

            final TrustManager[] trustAllCerts = new TrustManager[]{trustManager};
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .create();

            return new Retrofit.Builder()
                    .baseUrl(ROOT_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static LUSSISService getApiService() {
        return getRetrofitInstance().create(LUSSISService.class);
    }

    /**
     * Change the default IP address for server
     * @param address server IP address
     */
    public static void setIp(String address) {
        ROOT_URL = "https://" + address + "/LUSSIS/api/";
        retrofit = getRetrofitObj();
    }

}
