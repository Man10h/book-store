package com.Man10h.book_store.util;

import com.Man10h.book_store.model.entity.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@RequiredArgsConstructor
public class VNPayUtil {

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.payUrl}")
    private String payUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;


    public String createPaymentUrl(PaymentEntity paymentEntity) throws Exception {

        String vnp_TxnRef = paymentEntity.getId().toString();
        String vnp_Amount = String.valueOf((long) (paymentEntity.getAmount() * 100));

        Map<String, String> vnpParams = new HashMap<>();

        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Amount", vnp_Amount);
        vnpParams.put("vnp_CurrCode", "VND");

        vnpParams.put("vnp_TxnRef", vnp_TxnRef);
        vnpParams.put("vnp_OrderInfo", "Order id " + paymentEntity.getOrderEntity().getId());
        vnpParams.put("vnp_OrderType", "other");

        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_IpAddr", "127.0.0.1");

        // ✅ ĐÃ XÓA: vnpParams.put("vnp_SecureHashType", "HmacSHA512");
        // vnp_SecureHashType KHÔNG được đưa vào chuỗi hash

        String createDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        vnpParams.put("vnp_CreateDate", createDate);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 15);

        String expireDate = new SimpleDateFormat("yyyyMMddHHmmss").format(cal.getTime());
        vnpParams.put("vnp_ExpireDate", expireDate);

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {

            String fieldName = itr.next();
            String value = vnpParams.get(fieldName);

            if (value != null && !value.isEmpty()) {

                // Chuỗi để hash (KHÔNG encode)
                hashData.append(fieldName).append("=").append(value);

                // Chuỗi để tạo URL (CÓ encode)
                query.append(fieldName)
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8));

                if (itr.hasNext()) {
                    hashData.append("&");
                    query.append("&");
                }
            }
        }

        String secureHash = hmacSHA512(hashSecret, hashData.toString());

        // ✅ Append vnp_SecureHashType vào URL SAU KHI đã tính hash
        return payUrl + "?" + query
                + "&vnp_SecureHashType=HmacSHA512"
                + "&vnp_SecureHash=" + secureHash;
    }


    public static String hmacSHA512(String key, String data) throws Exception {

        Mac hmac512 = Mac.getInstance("HmacSHA512");

        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");

        hmac512.init(secretKey);

        byte[] rawHmac = hmac512.doFinal(data.getBytes());

        return Hex.encodeHexString(rawHmac);
    }


    public boolean verifySignature(Map<String, String> params) {

        String vnpSecureHash = params.get("vnp_SecureHash");

        if (vnpSecureHash == null) {
            return false;
        }

        Map<String, String> filteredParams = new HashMap<>();

        for (Map.Entry<String, String> entry : params.entrySet()) {

            String key = entry.getKey();

            // ✅ Loại bỏ cả vnp_SecureHash và vnp_SecureHashType khỏi chuỗi hash
            if (!"vnp_SecureHash".equals(key) && !"vnp_SecureHashType".equals(key)) {
                filteredParams.put(key, entry.getValue());
            }
        }

        try {

            List<String> fieldNames = new ArrayList<>(filteredParams.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();

            while (itr.hasNext()) {

                String fieldName = itr.next();
                String value = filteredParams.get(fieldName);

                if (value != null && !value.isEmpty()) {

                    hashData.append(fieldName).append("=").append(value);

                    if (itr.hasNext()) {
                        hashData.append("&");
                    }
                }
            }

            String computedHash = hmacSHA512(hashSecret, hashData.toString());

            return computedHash.equalsIgnoreCase(vnpSecureHash);

        } catch (Exception e) {
            return false;
        }
    }
}