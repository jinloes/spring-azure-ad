package com.jinloes.springazuread;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

import java.util.Map;

public interface AzureAdClient {
	@GET("v1.0/groups/{id}/members")
	Call<Map<String, Object>> getGroupMembers(@Header("Authorization") String token, @Path("id") String id);
}
