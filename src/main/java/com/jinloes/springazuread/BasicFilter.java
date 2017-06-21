package com.jinloes.springazuread;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.naming.ServiceUnavailableException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jinloes on 6/21/17.
 */
@Component
public class BasicFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private String clientId = "96d2dea3-e475-406c-9fcd-b3013ea67db6";
	private String clientSecret = "LiODvE+I78NGx6yQIlyQHRBUrNTQZlp345sia8B25Xk=";
	private String tenant = "tomigniteinc.onmicrosoft.com";
	private String authority = "https://login.windows.net/";

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			try {

				String currentUri = request.getScheme()
						+ "://"
						+ request.getServerName()
						+ ("http".equals(request.getScheme())
						&& request.getServerPort() == 80
						|| "https".equals(request.getScheme())
						&& request.getServerPort() == 443 ? "" : ":"
						+ request.getServerPort())
						+ httpRequest.getRequestURI();
				String fullUrl = currentUri
						+ (httpRequest.getQueryString() != null ? "?"
						+ httpRequest.getQueryString() : "");
				// check if user has a session
				if (!AuthHelper.isAuthenticated(httpRequest)) {
					if (AuthHelper.containsAuthenticationData(httpRequest)) {
						Map<String, String> params = new HashMap<String, String>();
						for (String key : request.getParameterMap().keySet()) {
							params.put(key, request.getParameterMap().get(key)[0]);
						}
						AuthenticationResponse authResponse = AuthenticationResponseParser.parse(new URI(fullUrl),
								params);
						if (AuthHelper.isAuthenticationSuccessful(authResponse)) {

							AuthenticationSuccessResponse oidcResponse = (AuthenticationSuccessResponse) authResponse;
							AuthenticationResult result = getAccessToken(oidcResponse.getAuthorizationCode(),
									currentUri);
							createSessionPrincipal(httpRequest, result);
						} else {
							AuthenticationErrorResponse oidcResponse = (AuthenticationErrorResponse) authResponse;
							throw new Exception(String.format("Request for auth code failed: %s - %s",
									oidcResponse.getErrorObject().getCode(), oidcResponse.getErrorObject()
											.getDescription()));
						}
					} else {
						// not authenticated
						httpResponse.setStatus(302);
						httpResponse.sendRedirect(getRedirectUrl(currentUri));
						return;
					}
				} else {
					// if authenticated, how to check for valid session?
					AuthenticationResult result = AuthHelper.getAuthSessionObject(httpRequest);

					if (httpRequest.getParameter("refresh") != null) {
						result = getAccessTokenFromRefreshToken(
								result.getRefreshToken(), currentUri);
					} else {
						if (httpRequest.getParameter("cc") != null) {
							result = getAccessTokenFromClientCredentials();
						} else {
							if (result.getExpiresOnDate().before(new Date())) {
								result = getAccessTokenFromRefreshToken(
										result.getRefreshToken(), currentUri);
							}
						}
					}
					createSessionPrincipal(httpRequest, result);
				}
			} catch (Throwable exc) {
				httpResponse.setStatus(500);
				request.setAttribute("error", exc.getMessage());
				httpResponse.sendRedirect(((HttpServletRequest) request)
						.getContextPath() + "/error.jsp");
			}
		}
		chain.doFilter(request, response);
	}

	private AuthenticationResult getAccessTokenFromClientCredentials() throws Throwable {
		AuthenticationContext context = null;
		AuthenticationResult result = null;
		ExecutorService service = null;
		try {
			service = Executors.newFixedThreadPool(1);
			context = new AuthenticationContext(authority + tenant + "/", true, service);
			Future<AuthenticationResult> future = context.acquireToken(
					"https://graph.windows.net", new ClientCredential(clientId, clientSecret), null);
			result = future.get();
			LOGGER.info("Token result: {}", result);
		} catch (ExecutionException e) {
			throw e.getCause();
		} finally {
			service.shutdown();
		}

		if (result == null) {
			throw new ServiceUnavailableException("authentication result was null");
		}
		return result;
	}

	private AuthenticationResult getAccessTokenFromRefreshToken(String refreshToken, String currentUri)
			throws Throwable {
		AuthenticationContext context = null;
		AuthenticationResult result = null;
		ExecutorService service = null;
		try {
			service = Executors.newFixedThreadPool(1);
			context = new AuthenticationContext(authority + tenant + "/", true,
					service);
			Future<AuthenticationResult> future = context
					.acquireTokenByRefreshToken(refreshToken,
							new ClientCredential(clientId, clientSecret), null,
							null);
			result = future.get();
		} catch (ExecutionException e) {
			throw e.getCause();
		} finally {
			service.shutdown();
		}

		if (result == null) {
			throw new ServiceUnavailableException("authentication result was null");
		}
		return result;

	}

	private AuthenticationResult getAccessToken(AuthorizationCode authorizationCode, String currentUri)
			throws Throwable {
		String authCode = authorizationCode.getValue();
		ClientCredential credential = new ClientCredential(clientId, clientSecret);
		AuthenticationContext context = null;
		AuthenticationResult result = null;
		ExecutorService service = null;
		try {
			service = Executors.newFixedThreadPool(1);
			context = new AuthenticationContext(authority + tenant + "/", true, service);
			Future<AuthenticationResult> future = context.acquireTokenByAuthorizationCode(authCode,
					new URI(currentUri), credential, null);
			result = future.get();
			LOGGER.info("Token result: {}", result);
		} catch (ExecutionException e) {
			throw e.getCause();
		} finally {
			service.shutdown();
		}

		if (result == null) {
			throw new ServiceUnavailableException("authentication result was null");
		}
		return result;
	}

	private void createSessionPrincipal(HttpServletRequest httpRequest, AuthenticationResult result) throws Exception {
		LOGGER.info("Token result: {}", result);
		httpRequest.getSession().setAttribute(AuthHelper.PRINCIPAL_SESSION_NAME, result);
	}

	private String getRedirectUrl(String currentUri) throws UnsupportedEncodingException {
		String redirectUrl = authority
				+ this.tenant
				+ "/oauth2/authorize?response_type=code%20id_token&scope=openid&response_mode=form_post&redirect_uri="
				+ URLEncoder.encode(currentUri, "UTF-8") + "&client_id="
				+ clientId + "&resource=https%3a%2f%2fmanagement.azure.com"
				+ "&nonce=" + UUID.randomUUID() + "&site_id=500879";
		return redirectUrl;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		//clientId = config.getInitParameter("client_id");
		//authority = config.getServletContext().getInitParameter("authority");
		//tenant = config.getServletContext().getInitParameter("tenant");
		//clientSecret = config.getInitParameter("secret_key");
	}

}
