package net.alanwei.telecom;

import io.reactivex.Observable;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import retrofit2.Response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AlipayQrCode {
    public static void getPayQrCodeUrl(Observable<Response<ResponseBody>> prevResponse) {
        CommonRequestFactory.CommonRequest req = CommonRequestFactory.create(null);

        final Map<String, String> alipayCookies = new HashMap<>();

        prevResponse.switchMap(response -> {
            //mapi.alipay.com -> Response
            //Request -> unitradeadapter.alipay.com

            Map<String, String> reqCookies = generateAlipayCookie(response.headers());
            reqCookies.keySet().forEach(key -> alipayCookies.put(key, reqCookies.get(key)));
            Map<String, String> headers = new HashMap<>();
            String cookieValue = String.format("zone=%s; ALIPAYJSESSIONID=%s; ctoken=%s", alipayCookies.get("zone"), alipayCookies.get("ALIPAYJSESSIONID"), alipayCookies.get("ctoken"));
            headers.put("Cookie", cookieValue);

            String location = response.headers().get("Location");
            return req.get(location, headers);
        }).switchMap(response -> {
            //unitradeadapter.alipay.com -> Response
            //Request -> excashier.alipay.com

            Map<String, String> reqCookies = generateAlipayCookie(response.headers());
            if (reqCookies.containsKey("ALIPAYJSESSIONID")) {
                alipayCookies.put("ALIPAYJSESSIONID", reqCookies.get("ALIPAYJSESSIONID"));
            }

            Map<String, String> headers = new HashMap<>();
            String cookieValue = String.format("zone=%s; ALIPAYJSESSIONID=%s; ctoken=%s", alipayCookies.get("zone"), alipayCookies.get("ALIPAYJSESSIONID"), alipayCookies.get("ctoken"));
            headers.put("Cookie", cookieValue);

            String location = response.headers().get("Location");
            return req.get(location, headers);
        }).subscribe(response -> {
            //excashier.alipay.com -> Response

            int resCode = response.code();

            String data = response.body().string();
            Document doc = Jsoup.parse(data);
            String qrImageUrl = doc.selectFirst("#J_qrImgUrl").attr("value");
            System.out.println("支付二维码图片地址: " + qrImageUrl);
        });

    }


    public static Map<String, String> generateAlipayCookie(Headers headers) {

        return headers.values("Set-Cookie")
                .stream()
                .map(cookie -> cookie.split(";")[0].split("="))
                .filter(kv -> Arrays.asList("zone", "ALIPAYJSESSIONID", "ctoken").indexOf(kv[0]) != -1)
                .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));

    }
}

