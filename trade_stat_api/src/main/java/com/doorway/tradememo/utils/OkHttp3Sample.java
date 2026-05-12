package com.doorway.tradememo.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import junit.framework.Test;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Description: 对 OkHttpClient 的简单封装，主要是返回值的处理，以及增加重试机制
 * @Author: Li Yalei - Robin
 * @Date: 2021/1/27 19:10
 */
@Slf4j
public class OkHttp3Sample implements Serializable {

    public final static OkHttp3Sample HTTP_CLIENT = OkHttp3Sample.newBuilder()
            // 设置一些OkHttp的初始参数
            .init(builder -> {
                // 设置读超时
                builder.readTimeout(30, TimeUnit.SECONDS);
                //设置写超时参数，等等
                builder.readTimeout(30, TimeUnit.SECONDS);
                //连接超时参数
                builder.connectTimeout(30, TimeUnit.SECONDS);
                //连接池配置
                builder.connectionPool(new ConnectionPool(50, 30, TimeUnit.MINUTES));

            })
            // 设置重试参数
            .retryable(3, 200L, OkHttp3Sample.RetryWhen.Timeout)
            //类型转换器，默认使用GSon的类型转换，可以自定义替换掉
//            .convert((responseBody, o) -> {
//                return "";
//            })
            .build();

    public final static OkHttp3Sample HTTPS_CLIENT = OkHttp3Sample.newBuilder().init(builder -> {

        SSLSocketClient client = new SSLSocketClient();
        builder.sslSocketFactory(client.getSSLSocketFactory(), client.getX509TrustManager());
        builder.hostnameVerifier(client.getHostnameVerifier());
        // 设置读超时
        builder.readTimeout(30, TimeUnit.SECONDS);
        //设置写超时参数，等等
        builder.readTimeout(30, TimeUnit.SECONDS);
        //连接超时参数
        builder.connectTimeout(30, TimeUnit.SECONDS);
        //连接池配置
        builder.connectionPool(new ConnectionPool(50, 30, TimeUnit.MINUTES));
    })
            .retryable(3, 200L, OkHttp3Sample.RetryWhen.Timeout)
            .build();


    private OkHttpClient httpClient;

    /**
     * 默认的类型转换器, 默认情况下，使用 gson 的转换器
     */
    private BiFunction<ResponseBody, Class<?>, Object> defaultTypeConvert =
            (responseBody, clazz) -> {
                try {
                    return new Gson().fromJson(responseBody.string(), clazz);
                } catch (IOException e) {
                    log.error("Failed to parse response body", e);
                    return null;
                }
            };

    private OkHttp3Sample(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setDefaultTypeConvert(BiFunction<ResponseBody, Class<?>, Object> defaultTypeConvert) {
        this.defaultTypeConvert = defaultTypeConvert;
    }


    /**
     * get 请求;
     * 注意：Silently意为：忽略异常，成功了就返回值，失败时或者异常时返回 null
     *
     * @param url 地址
     * @return
     */
    public String getSilently(String url) {
        return getSilently(url, String.class);
    }

    /**
     * get 请求;
     * 注意：Silently意为：忽略异常，失败时或者异常时返回 null
     *
     * @param url   地址
     * @param param 请求参数
     * @return
     */
    public String getSilently(String url, Map<String, Object> param) {
        return getSilently(url, param, String.class);
    }

    /**
     * get 请求;
     * 注意：Silently意为：忽略异常，失败时或者异常时返回 null
     *
     * @param url   地址
     * @param clazz 返回类型
     * @return
     */
    public <T> T getSilently(String url, Class<T> clazz) {
        return getSilently(url, null, clazz);
    }

    /**
     * get 请求;
     * 注意：Silently意为：忽略异常，失败时或者异常时返回 null
     *
     * @param url   地址
     * @param param 请求参数
     * @param clazz 返回类型
     * @return
     */
    public <T> T getSilently(String url, Map<String, Object> param, Class<T> clazz) {
        return get(url, param, null, clazz, null);
    }

    /**
     *
     * @param url
     * @param param
     * @param headers
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getSilently(String url, Map<String, Object> param, Map<String, String> headers, Class<T> clazz) {
        return get(url, param, headers, clazz, null);
    }

    /**
     * get or post 请求
     * 返回结果为输入流，注意关闭流
     *
     * @param requestType      post or get
     * @param url              地址
     * @param param            入参
     * @param exceptionHandler
     * @return
     */
    public InputStream inputStream(RequestType requestType, String url, Map<String, Object> param, Map<String, String> headers, Consumer<Exception> exceptionHandler) {
        switch (requestType) {
            case GET:
                return get(url, param, headers, response -> (response == null || !response.isSuccessful()) ? null : response.body().byteStream(), exceptionHandler);
            case POST:
                return post(url, param, headers, response -> (response == null || !response.isSuccessful()) ? null : response.body().byteStream(), exceptionHandler);
            default:
                throw new IllegalArgumentException("不支持的请求类型" + requestType);
        }
    }

    public InputStream inputStreamSilently(RequestType requestType, String url, Map<String, Object> param, Map<String, String> headers) {
        return inputStream(requestType, url, param, headers, null);
    }

    public String get(String url) {
        return get(url, null, null, String.class, exception -> {
            throw new RuntimeException(exception);
        });
    }

    public Response getResponse(String url, Map<String, Object> param) {
        return get(url, param, null, Response.class);
    }

    public String get(String url, Consumer<Exception> exceptionHandler) {
        return get(url, null, null, String.class, exceptionHandler);
    }

    public String get(String url, Map<String, Object> param,Consumer<Exception> exceptionHandler) {
        return get(url, param, null, String.class, exceptionHandler);
    }

    public String get(String url, Map<String, Object> param, Map<String, String> headers, Consumer<Exception> exceptionHandler) {
        return get(url, param, headers, String.class, exceptionHandler);
    }

    /**
     * @param url              请求地址
     * @param param            入参
     * @param response         响应处理器，二元处理，输入response, 输出 T
     * @param exceptionHandler 异常时的处理
     * @param <T>
     * @return
     */
    public <T> T get(String url, Map<String, Object> param, Map<String, String> headers, Function<Response, T> response, Consumer<Exception> exceptionHandler) {
        return response.apply(get(url, param, headers, Response.class, exceptionHandler));
    }

    public <T> T get(String url, Map<String, Object> param, Map<String, String> headers, Class<T> clazz) {
        return get(url, param, headers, clazz, exception -> {
            throw new RuntimeException(exception);
        });
    }


    /**
     * 发送post请求,
     * 注意：Silently意为：忽略异常，失败时或者异常时返回 null
     *
     * @param url
     * @param param
     * @return
     */
    public String postSilently(String url, Object param) {
        return post(url, param, null, String.class, null);
    }


    /**
     * 发送post请求,
     * 注意：Silently意为：忽略异常，失败时或者异常时返回 null
     *
     * @param url
     * @param param
     * @return
     */
    public String postSilently(String url, Object param, Map<String, String> headers) {
        return post(url, param, headers, String.class, null);
    }

    public String post(String url, Object param) {
        return post(url, param, null, String.class, exception -> {
            throw new RuntimeException(exception);
        });
    }

    public String post(String url, Object param, Map<String, String> headers) {
        return post(url, param, headers, String.class, exception -> {
            throw new RuntimeException(exception);
        });
    }

    public String post(String url, Object param, Map<String, String> headers, Consumer<Exception> exceptionHandler) {
        return post(url, param, headers, String.class, exceptionHandler);
    }

    /**
     * @param url              请求地址
     * @param param            入参
     * @param response         响应处理器，二元处理，输入response, 输出 T
     * @param exceptionHandler 异常处理
     * @param <T>
     * @return
     */
    public <T> T post(String url, Object param, Map<String, String> headers, Function<Response, T> response, Consumer<Exception> exceptionHandler) {
        return response.apply(post(url, param, headers, Response.class, exceptionHandler));
    }

    public <T> T post(String url, Object param, Map<String, String> headers, Class<T> clazz, Consumer<Exception> exceptionHandler) {
        return post(url, param, headers, clazz, this::doNothing, exceptionHandler);
    }


    private void doNothing(Object input) {
    }

    /**
     * get 请求
     *
     * @param url
     * @param param
     * @param clazz
     * @param exceptionHandler
     * @param <T>
     * @return
     */
    public <T> T get(String url, Map<String, Object> param, Map<String, String> headers, Class<T> clazz, Consumer<Exception> exceptionHandler) {

        if (param == null) {
            param = new HashMap<>();
        }

        List<String> params = new ArrayList<>();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            params.add(entry.getKey() + "=" + entry.getValue());
        }
        String paramStr = StringUtils.join(params, "&");
        url = url.trim();
        if (!url.endsWith("?") && url.lastIndexOf("?") == -1) {
            url += "?";
        }
        if (!param.isEmpty()) {
            url += url.endsWith("?") ? paramStr : "&" + paramStr;
        }
        if (CollectionUtils.isEmpty(headers)) {
            headers = new HashMap<>();
            headers.put("Content-Type", "application/json;charset=UTF-8");
            headers.put("Cache-Control", "no-cache");
            headers.put("Connection", "keep-alive");
        }
        Request request = new Request.Builder().url(url).headers(getHeadersFrom(headers)).get().build();

        return requestInternal(param, () -> request, clazz, exceptionHandler);
    }



    private static Headers getHeadersFrom(Map<String, String> headers) {
        Headers.Builder builder = new Headers.Builder();
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach(builder::add);
        }
        return builder.build();
    }

    /**
     * post请求
     *
     * @param url              地址
     * @param param            参数
     * @param clazz            返回类型
     * @param requestPreSet    request参数预设
     * @param exceptionHandler 异常时的处理
     * @param <T>
     * @return
     */
    public <T> T post(String url, Object param, Map<String, String> headers, Class<T> clazz, Consumer<Request.Builder> requestPreSet, Consumer<Exception> exceptionHandler) {
        return requestInternal(param,
                () -> {
                    Request.Builder requestBuilder = new Request.Builder().url(url).headers(getHeadersFrom(headers))
                            .post(RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(param)));
                    requestPreSet.accept(requestBuilder);
                    return requestBuilder.build();
                }
                , clazz, exceptionHandler);
    }




    /**
     * 发送post请求,
     * 注意：Silently意为：忽略异常，失败时或者异常时返回 null
     *
     * @param url
     * @param param
     * @return
     */
    public String putSilently(String url, Object param) {
        return put(url, param, null, String.class, null);
    }


    /**
     * 发送post请求,
     * 注意：Silently意为：忽略异常，失败时或者异常时返回 null
     *
     * @param url
     * @param param
     * @return
     */
    public String putSilently(String url, Object param, Map<String, String> headers) {
        return put(url, param, headers, String.class, null);
    }

    public String put(String url, Object param) {
        return put(url, param, null, String.class, exception -> {
            throw new RuntimeException(exception);
        });
    }

    public String put(String url, Object param, Map<String, String> headers) {
        return put(url, param, headers, String.class, exception -> {
            throw new RuntimeException(exception);
        });
    }

    public String put(String url, Object param, Map<String, String> headers, Consumer<Exception> exceptionHandler) {
        return put(url, param, headers, String.class, exceptionHandler);
    }

    /**
     * @param url              请求地址
     * @param param            入参
     * @param response         响应处理器，二元处理，输入response, 输出 T
     * @param exceptionHandler 异常处理
     * @param <T>
     * @return
     */
    public <T> T put(String url, Object param, Map<String, String> headers, Function<Response, T> response, Consumer<Exception> exceptionHandler) {
        return response.apply(put(url, param, headers, Response.class, exceptionHandler));
    }

    public <T> T put(String url, Object param, Map<String, String> headers, Class<T> clazz, Consumer<Exception> exceptionHandler) {
        return post(url, param, headers, clazz, this::doNothing, exceptionHandler);
    }



    public <T> T put(String url, Object param, Map<String, String> headers, Class<T> clazz, Consumer<Request.Builder> requestPreSet, Consumer<Exception> exceptionHandler) {
        return requestInternal(param,
                () -> {
                    Request.Builder requestBuilder = new Request.Builder().url(url).headers(getHeadersFrom(headers))
                            .put(RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(param)));
                    requestPreSet.accept(requestBuilder);
                    return requestBuilder.build();
                }
                , clazz, exceptionHandler);
    }


    public String deleteSilently(String url) {
        return delete(url, null, null, String.class, null);
    }

    public String deleteSilently(String url, Map<String, String> headers) {
        return delete(url, null, headers, String.class, null);
    }


    /**
     * 发送post请求,
     * 注意：Silently意为：忽略异常，失败时或者异常时返回 null
     *
     * @param url
     * @param param
     * @return
     */
    public String deleteSilently(String url, Object param) {
        return delete(url, param, null, String.class, null);
    }


    /**
     * 发送post请求,
     * 注意：Silently意为：忽略异常，失败时或者异常时返回 null
     *
     * @param url
     * @param param
     * @return
     */
    public String deleteSilently(String url, Object param, Map<String, String> headers) {
        return delete(url, param, headers, String.class, null);
    }

    public String delete(String url, Object param) {
        return delete(url, param, null, String.class, exception -> {
            throw new RuntimeException(exception);
        });
    }

    public String delete(String url, Object param, Map<String, String> headers) {
        return delete(url, param, headers, String.class, exception -> {
            throw new RuntimeException(exception);
        });
    }

    public String delete(String url, Object param, Map<String, String> headers, Consumer<Exception> exceptionHandler) {
        return delete(url, param, headers, String.class, exceptionHandler);
    }

    /**
     * @param url              请求地址
     * @param param            入参
     * @param response         响应处理器，二元处理，输入response, 输出 T
     * @param exceptionHandler 异常处理
     * @param <T>
     * @return
     */
    public <T> T delete(String url, Object param, Map<String, String> headers, Function<Response, T> response, Consumer<Exception> exceptionHandler) {
        return response.apply(delete(url, param, headers, Response.class, exceptionHandler));
    }

    public <T> T delete(String url, Object param, Map<String, String> headers, Class<T> clazz, Consumer<Exception> exceptionHandler) {
        return delete(url, param, headers, clazz, this::doNothing, exceptionHandler);
    }


    public <T> T delete(String url, Object param, Map<String, String> headers, Class<T> clazz, Consumer<Request.Builder> requestPreSet, Consumer<Exception> exceptionHandler) {
        return requestInternal(param,
                () -> {
                    Request.Builder requestBuilder = new Request.Builder().url(url).headers(getHeadersFrom(headers))
                            .delete(RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(param)));
                    requestPreSet.accept(requestBuilder);
                    return requestBuilder.build();
                }
                , clazz, exceptionHandler);
    }


    /**
     * 发送post请求,
     * 注意：Silently意为：忽略异常，失败时或者异常时返回 null
     *
     * @param url
     * @param param
     * @return
     */
    public String patchSilently(String url, Object param) {
        return patch(url, param, null, String.class, null);
    }


    /**
     * 发送post请求,
     * 注意：Silently意为：忽略异常，失败时或者异常时返回 null
     *
     * @param url
     * @param param
     * @return
     */
    public String patchSilently(String url, Object param, Map<String, String> headers) {
        return patch(url, param, headers, String.class, null);
    }

    public String patch(String url, Object param) {
        return patch(url, param, null, String.class, exception -> {
            throw new RuntimeException(exception);
        });
    }

    public String patch(String url, Object param, Map<String, String> headers) {
        return patch(url, param, headers, String.class, exception -> {
            throw new RuntimeException(exception);
        });
    }

    public String patch(String url, Object param, Map<String, String> headers, Consumer<Exception> exceptionHandler) {
        return patch(url, param, headers, String.class, exceptionHandler);
    }

    /**
     * @param url              请求地址
     * @param param            入参
     * @param response         响应处理器，二元处理，输入response, 输出 T
     * @param exceptionHandler 异常处理
     * @param <T>
     * @return
     */
    public <T> T patch(String url, Object param, Map<String, String> headers, Function<Response, T> response, Consumer<Exception> exceptionHandler) {
        return response.apply(patch(url, param, headers, Response.class, exceptionHandler));
    }

    public <T> T patch(String url, Object param, Map<String, String> headers, Class<T> clazz, Consumer<Exception> exceptionHandler) {
        return patch(url, param, headers, clazz, this::doNothing, exceptionHandler);
    }



    public <T> T patch(String url, Object param, Map<String, String> headers, Class<T> clazz, Consumer<Request.Builder> requestPreSet, Consumer<Exception> exceptionHandler) {
        return requestInternal(param,
                () -> {
                    Request.Builder requestBuilder = new Request.Builder().url(url).headers(getHeadersFrom(headers))
                            .patch(RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(param)));
                    requestPreSet.accept(requestBuilder);
                    return requestBuilder.build();
                }
                , clazz, exceptionHandler);
    }




    private <T> T requestInternal(Object param, Supplier<Request> requestSupplier, Class<T> clazz,
                                  Consumer<Exception> exceptionHandler) {
        if (param == null) {
            log.error("param cannot be null");
            return null;
        }
        Request request = requestSupplier.get();
        log.info("执行HTTP请求"+request.toString());
        try {
            Response response = httpClient.newCall(request).execute();
            if (Response.class.isAssignableFrom(clazz)) {
                return (T) response;
            }
            try {
                ResponseBody body = response.body();
                if (String.class == clazz) {
                    return ((T) body.string());
                }
                return (T) defaultTypeConvert.apply(body, clazz);
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        } catch (Exception e) {
            log.error("http post request error", e);
            if (exceptionHandler != null) {
                exceptionHandler.accept(e);
            }
            return null;
        }
    }




    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private RetryInterceptor retryInterceptor;

        private BiFunction typeConvert;

        private OkHttpClient.Builder builder;

        Builder() {
            this.builder = new OkHttpClient().newBuilder();
        }

        public Builder retryable(int retryMaxTimes, long retryInterval, RetryWhen when) {
            Objects.requireNonNull(when, "retry when required");
            this.retryInterceptor = new RetryInterceptor(retryMaxTimes, retryInterval, when);
            this.builder.addInterceptor(this.retryInterceptor);
            return this;
        }

        public <T, R> Builder convert(BiFunction<ResponseBody, T, R> convert) {
            this.typeConvert = convert;
            return this;
        }

        public Builder init(Consumer<OkHttpClient.Builder> builderConsumer) {
            builderConsumer.accept(this.builder);
            return this;
        }

        public OkHttp3Sample build() {
            OkHttp3Sample okHttp3Sample = new OkHttp3Sample(builder.build());
            if (this.typeConvert != null) {
                okHttp3Sample.setDefaultTypeConvert(this.typeConvert);
            }
            return okHttp3Sample;
        }
    }

    public enum RequestType {
        GET, POST;
    }

    /**
     * 重试的条件
     */
    public enum RetryWhen {
        /* 超时重试 */
        Timeout,
        /* 只要发生异常就重试 */
        Exception;

        public boolean shouldRetry(Throwable throwable) {
            if (throwable == null) {
                return false;
            }
            if (throwable instanceof SocketTimeoutException ||
                    java.lang.Exception.class.isAssignableFrom(throwable.getClass())) {
                return true;
            }
            return false;
        }
    }

    /**
     * 仅当发生异常时，重试才会生效
     */
    static class RetryInterceptor implements Interceptor {

        //最大重试次数
        private int retryMaxTimes;
        //重试的间隔
        private long retryInterval;

        private RetryWhen retryWhen;

        /**
         * @param retryMaxTimes
         * @param retryInterval
         * @param when
         */
        public RetryInterceptor(int retryMaxTimes, long retryInterval, RetryWhen when) {
            this.retryMaxTimes = retryMaxTimes;
            this.retryInterval = retryInterval;
            this.retryWhen = when;
        }

        /**
         * 指数退避算法间隔时间逐渐翻倍
         *
         * @param retryTime
         * @return
         */
        public long getRetryInterval(int retryTime) {
            return retryInterval * retryTime * retryTime;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = null;

            int retryNum = 0;
            //失败时重试才会生效
            while ((response == null || !response.isSuccessful()) &&
                    retryNum < retryMaxTimes) {
                try {
                    response = chain.proceed(request);
                } catch (Throwable throwable) {
                    log.error("请求发生异常，param=" + request.toString(), throwable);
                    boolean retry = false;
                    if (retryWhen != null && retryWhen.shouldRetry(throwable)) {
                        retry = true;
                    }
                    if (!retry || retryNum == retryMaxTimes) {
                        //不需要重试, 或者重试达到最大次数
                        throw throwable;
                    }
                    log.warn("请求失败，开始重试{}, 最大重试次数{}", retryNum + 1, retryMaxTimes);
                    final long nextInterval = getRetryInterval(retryNum);
                    try {
                        log.info("Wait for {}", nextInterval);
                        Thread.sleep(nextInterval);
                    } catch (final InterruptedException e) {
                        log.error("等待重试时，发生中断异常", e);
                        throw new RuntimeException(e);
                    }
                    retryNum++;
                }
            }
            return response;
        }

    }

    public static class SSLSocketClient {

        //获取这个SSLSocketFactory
        public SSLSocketFactory getSSLSocketFactory() {
            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, getTrustManager(), new SecureRandom());
                return sslContext.getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //获取TrustManager
        private TrustManager[] getTrustManager() {
            return new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };
        }

        //获取HostnameVerifier
        public HostnameVerifier getHostnameVerifier() {
            return (s, sslSession) -> true;
        }

        public X509TrustManager getX509TrustManager() {
            X509TrustManager trustManager = null;
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
                trustManager = (X509TrustManager) trustManagers[0];
            } catch (Exception e) {
                e.printStackTrace();
            }

            return trustManager;
        }
    }

    public static void main(String[] args) {
        /* ------ post请求 ------- */
        Map<String, Object> param = new HashMap<>();
//1.带参数请求
//        String result = OkHttp3Simple.DEFAULT.postSilently("https://www.baidu.com", param);
//2. 类型转换 + 异常处理
        String result = OkHttp3Sample.HTTP_CLIENT.get("https://smartbox.gtimg.cn/s3/?q=513530&t=all");
//3.更多方法重载，见源码
        System.out.println(result);
        /* ------- 扩展用法 ------ */
//        OkHttp3Sample okHttp3Sample = OkHttp3Sample.newBuilder()
//                // 设置一些OkHttp的初始参数
//                .init(builder -> {
//                    // 设置读超时
//                    builder.readTimeout(60, TimeUnit.MILLISECONDS);
//                    //设置写超时参数，等等
//                    builder.connectTimeout(30,TimeUnit.SECONDS);
//
//                    builder.connectionPool(new ConnectionPool(50,30,TimeUnit.SECONDS));
//
//                })
//                // 设置重试参数
//                .retryable(3, 2000L, OkHttp3Sample.RetryWhen.Timeout)
//                //类型转换器，默认使用GSon的类型转换，可以自定义替换掉
//                .convert((responseBody, o) -> {
//                    return "";
//                })
//                .build();
//
//        /* ------- 重试机制 -------*/
////重试的话，需要先构造一个支持重试的okHttp3Simple对象
//        OkHttp3Sample okHttp3Sample2 = OkHttp3Sample.newBuilder()
//                .init(builder -> {
//                    // 对httpClient初始，根据需要调用
//                })
//                .retryable(3, 2000L, OkHttp3Sample.RetryWhen.Timeout)
//                .build();
////此时的对象支持，当请求超时的时候，重试三次，每次间隔2s
//        okHttp3Sample.get("https://www.baidu.com");
////如果想要根据返回值来决定是否重试，需要自己在返回值处理的时候，抛出异常来触发重试机制(同时上方的RetryWhen应该选在Exception类型)。比如
//        okHttp3Sample.get("https://www.baidu.com", param, response -> {
//            assert response.body() != null;
//            try {
//                JSONObject ss = JSONObject.parseObject(response.body().string());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
////            if(条件){
////                // 返回值判断，并抛出异常。
////                throw new RuntimeException();
////            }
////            return 返回值;
//            return true;
//        }, null);
//
//        /* ---------- 如果 https 开头的地址报错，可以使用SSL内置的对象 ----------- */
//        OkHttp3Sample.HTTPS_CLIENT.get("https://www.baidu.com");

        /* ---------- 如果枚举的重试类型，可以只用修改RetryWhen枚举类即可 -------*/
    }
}