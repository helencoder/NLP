package giiso;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicHeader;

import java.util.Map;


/**
 * 
 * @author Robin Wu
 *
 */
public class APIRequest {	

	private String secretId;
	private String secretKey;

	
	public APIRequest(String secretId,String secretKey){
		this.secretId=secretId;
		this.secretKey=secretKey;
	}
	
	public String get(String url) throws Exception{
		Request req = Request.Get(url);		
		req.addHeader(new BasicHeader("contentType", APIClient.CHARSET));
		req.addHeader(new BasicHeader("Accept-Charset", APIClient.CHARSET));
		if(secretId!=null&&secretKey!=null) {
			APIClient.buildRequestHeader(req,secretId,secretKey);
		}
		Response respon = req.execute();		
		return respon.returnContent().asString();
		
	}
	
	 public  String post(String url,Map<String,String> map) throws Exception{
			
			Request req = Request.Post(url);		
			req.addHeader(new BasicHeader("contentType", APIClient.CHARSET));
			req.addHeader(new BasicHeader("Accept-Charset", APIClient.CHARSET));
			
			if(secretId!=null&&secretKey!=null) {
			  APIClient.buildRequestHeader(req,secretId,secretKey);
			}
			
			Form form=Form.form();		
			 for (Map.Entry<String, String> entry : map.entrySet()) {
				 entry.getKey();
				 form.add( entry.getKey(),entry.getValue());
			 }
			
			req.bodyForm(form.build());
				
				Response respon = req.execute();		
				return respon.returnContent().asString();
			
		}
		
		

}
