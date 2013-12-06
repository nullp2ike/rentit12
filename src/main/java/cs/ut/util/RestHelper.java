package cs.ut.util;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;

import com.sun.jersey.core.util.Base64;

public class RestHelper {
	
	public static HttpHeaders getHeaders(String username, String password) {
		String auth = username + ":" + password;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays
				.asList(org.springframework.http.MediaType.APPLICATION_JSON));
		byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
		headers.add("Authorization", "Basic "
				+ new String(encodedAuthorisation));
		return headers;
	}

}
