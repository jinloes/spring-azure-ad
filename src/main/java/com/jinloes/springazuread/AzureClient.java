package com.jinloes.springazuread;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

import java.util.Map;

public interface AzureClient {
	@Headers({"Content-Type: application/json; charset=utf-8",
			"x-ms-logging-context: com.microsoft.azure.management.monitor.ActivityLogs list"})
	@GET("subscriptions/{subscriptionId}/providers/microsoft.insights/eventtypes/management/values")
	Call<Map<String, Object>> list(@Header("Authorization") String token,
			@Path("subscriptionId") String subscriptionId,
			@Query("api-version") String apiVersion);

	@Headers({"Content-Type: application/json; charset=utf-8",
			"x-ms-logging-context: com.microsoft.azure.management.monitor.ActivityLogs list"})
	@GET("subscriptions/{subscriptionId}/providers/microsoft.insights/eventtypes/management/values")
	Call<Map<String, Object>> list(@Header("Authorization") String token,
			@Path("subscriptionId") String subscriptionId,
			@Query("api-version") String apiVersion, @Query("$filter") String filter,
			@Query("$select") String select);

	@Headers({"Content-Type: application/json; charset=utf-8",
			"x-ms-logging-context: com.microsoft.azure.management.monitor.ActivityLogs listNext"})
	@GET
	Observable<Response<ResponseBody>> listNext(@Url String nextUrl);
}
