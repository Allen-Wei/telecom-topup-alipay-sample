package net.alanwei.telecom;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.util.HashMap;
import java.util.Map;

public final class CommonRequestFactory {
    private static Map<String, CommonRequest> requests = new HashMap<>();

    private CommonRequestFactory() {
    }

    public synchronized static CommonRequest create(String baseUrl) {
        if (baseUrl == null) baseUrl = "https://mapi.alipay.com/";
        if (requests.containsKey(baseUrl)) {
            return requests.get(baseUrl);
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.followRedirects(false);

        CommonRequest req = new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(builder.build())
                .build()
                .create(CommonRequest.class);

        requests.put(baseUrl, req);
        return req;
    }


    public interface CommonRequest {

        @GET
        @Headers({"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36"})
        Observable<Response<ResponseBody>> get(@Url String url, @HeaderMap Map<String, String> headers);

        @POST
        @Headers({"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36"})
        @FormUrlEncoded
        Observable<Response<ResponseBody>> postForm(@Url String url, @HeaderMap Map<String, String> headers, @FieldMap Map<String, Object> body);
    }
}

