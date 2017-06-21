package com.jinloes.springazuread;

import com.microsoft.aad.adal4j.AuthenticationResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by jinloes on 6/21/17.
 */
@Controller
@RequestMapping("/secure/aad")
public class AadController {

	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public String getDirectoryObjects(ModelMap model, HttpServletRequest httpRequest) {
		HttpSession session = httpRequest.getSession();
		AuthenticationResult result = (AuthenticationResult) session.getAttribute(AuthHelper.PRINCIPAL_SESSION_NAME);
		if (result == null) {
			model.addAttribute("error", new Exception("AuthenticationResult not found in session."));
			return "/error";
		} else {
			String data;
			try {
				data = this.getUsernamesFromGraph(result.getAccessToken(), "tomigniteinc.onmicrosoft.com");
				model.addAttribute("users", data);
			} catch (Exception e) {
				model.addAttribute("error", e);
				return "/error";
			}
		}
		return "/greeting";
	}

	private String getUsernamesFromGraph(String accessToken, String tenant) throws Exception {
		URL url = new URL(String.format("https://graph.windows.net/%s/users?api-version=2013-04-05", tenant,
				accessToken));

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// Set the appropriate header fields in the request header.
		conn.setRequestProperty("api-version", "2013-04-05");
		conn.setRequestProperty("Authorization", accessToken);
		conn.setRequestProperty("Accept", "application/json;odata=minimalmetadata");
		String goodRespStr = HttpClientHelper.getResponseStringFromConn(conn, true);
		// logger.info("goodRespStr ->" + goodRespStr);
		int responseCode = conn.getResponseCode();
		JSONObject response = HttpClientHelper.processGoodRespStr(responseCode, goodRespStr);
		JSONArray users = new JSONArray();

		users = JSONHelper.fetchDirectoryObjectJSONArray(response);

		StringBuilder builder = new StringBuilder();
		User user = null;
		for (int i = 0; i < users.length(); i++) {
			JSONObject thisUserJSONObject = users.optJSONObject(i);
			user = new User();
			JSONHelper.convertJSONObjectToDirectoryObject(thisUserJSONObject, user);
			builder.append(user.getUserPrincipalName() + "<br/>");
		}
		return builder.toString();
	}

}
