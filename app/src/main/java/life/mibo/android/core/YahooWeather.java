/*
 *  Created by Sumeet Kumar on 5/12/20 12:09 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/12/20 12:09 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.core;

import android.content.Context;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import life.mibo.android.models.weather.OpenWeather;
import life.mibo.android.ui.base.ItemClickListener;
import life.mibo.hardware.core.Logger;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class YahooWeather {

    final String AppId = "v6lUty30";
    final String ClientId = "dj0yJmk9dWFNOHp0Rk9iOTZxJmQ9WVdrOWRqWnNWWFI1TXpBbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PTE0";
    final String Secret = "45fa89ae56212b17d9d4df99bb475fa99b418ee9";


    public static void load(Double lat, Double lon) {
        String AppId = "v6lUty30";
        String ClientId = "dj0yJmk9dWFNOHp0Rk9iOTZxJmQ9WVdrOWRqWnNWWFI1TXpBbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PTE0";
        String Secret = "45fa89ae56212b17d9d4df99bb475fa99b418ee9";
        String url = "https://weather-ydn-yql.media.yahoo.com/forecastrss?lat=" + lat + "&lon=" + lon + "&format=json";

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

///addHeader("Authorization", "")
        Request.Builder request = new Request.Builder().url(url)
                .header("X-Yahoo-App-Id", AppId).header("Content-Type", "application/json")
                .header("oauth_consumer_key", ClientId).header("oauth_accessor_secret", Secret).get();
        Request r = request.build();

        ///OAuthConstants.GRANT_TYPE_CLIENT_CREDENTIALS
        //Headers.of()
        OkHttpClient client = new OkHttpClient();
        client.newCall(r).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Logger.e("onFailure " + call);
                Logger.e("onFailure exception: " + e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Logger.e("onResponse " + call);
                Logger.e("onFailure response: " + response);
            }
        });


    }

    // TODO change to

    public static void OpenApiWeather(Context context, Double lat, Double lon, ItemClickListener<String> listener) {
        if (lat == null || lon == null)
            return;

        try {
            String date = new SimpleDateFormat("yymmddhh").format(new Date());
            Logger.e("OpenApiWeather date " + date);
            String w = Prefs.getTemp(context).get("weather_" + date);
            if (w != null && w.length() > 0) {
                if (listener != null)
                    listener.onItemClicked(w, 0);
                return;
            }
            // String url = "http://samples.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=439d4b804bc8187953eb36d2a8c26a02";
            String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=4f33a71827ee7f7ef81cc77642237495";
            String icon = "http://openweathermap.org/img/wn/10d@2x.png";
            //273.15
            OkHttpClient client = new OkHttpClient();
            client.newCall(new Request.Builder().url(url).get().build()).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ResponseBody body = response.body();
                    if (body != null) {
                        try {
                            OpenWeather weather = new Gson().fromJson(body.string(), OpenWeather.class);
                            int we = (int) (weather.getMain().getTemp() - 273.15);
                            Prefs.getTemp(context).set("weather_" + date, "" + we);
                            if (listener != null)
                                listener.onItemClicked("" + we, 2);
                        } catch (Exception e) {

                        }
                    }
                }
            });
        } catch (Exception e) {

        }


    }

//
//    public class ExampleRequest<T> extends JsonRequest<T> {
//
//        final String appId = "test-app-id";
//        final String CONSUMER_KEY = "your-consumer-key";
//        final String CONSUMER_SECRET = "your-consumer-secret";
//        final String baseUrl = "https://weather-ydn-yql.media.yahoo.com/forecastrss";
//
//        public ExampleRequest(int method, String url, String requestBody, Response.Listener<T> listener, Response.ErrorListener errorListener) {
//            super(method, url, requestBody, listener, errorListener);
//        }
//
//        @Override
//        public Map<String, String> getHeaders() throws AuthFailureError {
//            Map<String, String> headers = new HashMap<>();
//            OAuthConstants
//            OAuthConsumer consumer = new OAuthConsumer(null, CONSUMER_KEY, CONSUMER_SECRET, null);
//            consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
//            OAuthAccessor accessor = new OAuthAccessor(consumer);
//            try {
//                OAuthMessage request = accessor.newRequestMessage(OAuthMessage.GET, getUrl(), null);
//                String authorization = request.getAuthorizationHeader(null);
//                headers.put("Authorization", authorization);
//            } catch (OAuthException e |IOException | URISyntaxException e) {
//                throw new AuthFailureError(e.getMessage());
//            }
//
//            headers.put("X-Yahoo-App-Id", appId);
//            headers.put("Content-Type", "application/json");
//            return headers;
//        }
//
//        @Override
//        public String getUrl() {
//            return baseUrl + "?location=sunnyvale,ca&format=json";
//        }
//
//        @Override
//        protected Response<T> parseNetworkResponse(NetworkResponse response) {
//            try {
//                String json = new String(
//                        response.data,
//                        HttpHeaderParser.parseCharset(response.headers));
//                T parsedResponse = parseResponse(json);
//                return Response.success(
//                        parsedResponse,
//                        HttpHeaderParser.parseCacheHeaders(response));
//            } catch (UnsupportedEncodingException | JsonSyntaxException e) {
//                return Response.error(new ParseError(e));
//            }
//        }
//
//        private T parseResponse(String jsonObject) {
//            return null; // Add response parsing here
//        }
//    }
}
