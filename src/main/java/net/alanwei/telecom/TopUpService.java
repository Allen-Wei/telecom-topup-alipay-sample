package net.alanwei.telecom;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public final class TopUpService {

    public static Observable<Response<ResponseBody>> captcha(String dateStamp, String cookies) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookies);
        return CommonRequestFactory.create(null).get("http://www.189.cn/portal/captcha/aircz.do?date=" + dateStamp, headers);
    }

    public static Observable<Response<ResponseBody>> doTrade(String mobile, String captcha, int amount, String cookies) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookies);

        Map<String, Object> body = new HashMap<>();
        body.put("accountNumber", mobile);
        body.put("shopId", "10036");
        body.put("payAmount", amount);
        body.put("sp", "1");
        body.put("checkPassCode", captcha);
        body.put("attributes[0][isTogetherRecharge]", "0");
        return CommonRequestFactory.create(null).postForm("http://www.189.cn/trade/recharge/bank.do", headers, body);
    }

    public static Observable<Response<ResponseBody>> doOrder(String orderId, String cookies) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", cookies);
        Map<String, Object> body = new HashMap<>();
        body.put("orderId", orderId);
        body.put("bankCode", "ALIPAY_J");
        body.put("paymentCode", "111");
        return CommonRequestFactory.create(null).postForm("http://www.189.cn/trade/payment/pay.do", headers, body);
    }

}

