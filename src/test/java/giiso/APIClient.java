package giiso;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicHeader;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author Robin Wu
 *
 */
public class APIClient {

	public static final String CHARSET = "UTF-8";
	public static final String ZONE_TIME = "EEE, dd MMM yyyy HH:mm:ss z";
	public static final String AUTH_TYPE = "HmacSHA1";

	public static String get(String url) throws Exception {
		return get(url, null, null);
	}

	public static String get(String url, String secretId, String secretKey) throws Exception {

		Request req = Request.Get(url);
		req.addHeader(new BasicHeader("contentType", CHARSET));
		req.addHeader(new BasicHeader("Accept-Charset", CHARSET));
		if (secretId != null && secretKey != null) {
			buildRequestHeader(req, secretId, secretKey);
		}
		Response respon = req.execute();
		return respon.returnContent().asString();

	}

	public static String post(String url) throws Exception {
		return post(url, null, null, null);
	}

	public static String post(String url, Map<String, String> map) throws Exception {
		return post(url, null, null, map);
	}

	public static String post(String url, String secretId, String secretKey, Map<String, String> map) throws Exception {

		Request req = Request.Post(url);
		req.addHeader(new BasicHeader("contentType", CHARSET));
		req.addHeader(new BasicHeader("Accept-Charset", CHARSET));

		if (secretId != null && secretKey != null) {
			buildRequestHeader(req, secretId, secretKey);
		}

		Form form = Form.form();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			form.add(entry.getKey(), entry.getValue());
		}
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form.build(), CHARSET);
		req.body(entity);
		
		Response respon = req.execute();
		return respon.returnContent().asString();

	}

	public static void buildRequestHeader(Request req, String secretId, String secretKey) throws Exception {
		Mac hmacSha1 = Mac.getInstance(AUTH_TYPE);
		byte[] keyBytes = secretKey.getBytes();
		hmacSha1.init(new SecretKeySpec(keyBytes, AUTH_TYPE));
		SimpleDateFormat sdf = new SimpleDateFormat(ZONE_TIME, Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
		String dateStr = sdf.format(new Date());
		String singSrc = "date: " + dateStr;
		String singStr = new String(Base64.getEncoder().encode(hmacSha1.doFinal(singSrc.getBytes())));

		String authorization = "hmac username=\"" + secretId
				+ "\", algorithm=\"hmac-sha1\", headers=\"date\", signature=\"" + singStr + "\"";
		req.addHeader(new BasicHeader("date", dateStr));
		req.addHeader(new BasicHeader("Authorization", authorization));
	}

	public static void main(String[] args) throws Exception {
		Map<String, String> params = new HashMap<>();
		params.put("keyword", "腾讯");
		
		String result = APIClient.post("http://api.giiso.ai/openapi/news/search", "29b5c3b07f864ed78f0597a6efddd7d7",
				"5f6882f51416433d9481df2e6bcb7192", params);
		System.out.println(result);
	}
}
