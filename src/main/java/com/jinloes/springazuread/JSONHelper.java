package com.jinloes.springazuread;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by jinloes on 6/21/17.
 */
public class JSONHelper {

	private static Logger logger = LoggerFactory.getLogger(JSONHelper.class);

	JSONHelper() {
		// PropertyConfigurator.configure("log4j.properties");
	}

	/**
	 * This method parses a JSON array out of a collection of JSON objects
	 * within a string.
	 *
	 * @param jSonData
	 *            The JSON string that holds the collection
	 * @return A JSON array that contains all the collection objects
	 * @throws Exception
	 */
	public static JSONArray fetchDirectoryObjectJSONArray(JSONObject jsonObject) throws Exception {
		JSONArray jsonArray = new JSONArray();
		jsonArray = jsonObject.optJSONObject("responseMsg").optJSONArray("value");
		return jsonArray;
	}

	/**
	 * This method parses a JSON object out of a collection of JSON objects
	 * within a string.
	 *
	 * @param jsonObject
	 * @return A JSON object that contains the DirectoryObject
	 * @throws Exception
	 */
	public static JSONObject fetchDirectoryObjectJSONObject(JSONObject jsonObject) throws Exception {
		JSONObject jObj = new JSONObject();
		jObj = jsonObject.optJSONObject("responseMsg");
		return jObj;
	}

	/**
	 * This method parses the skip token from a JSON-formatted string.
	 *
	 * @param jsonData
	 *            The JSON-formatted string
	 * @return The skipToken
	 * @throws Exception
	 */
	public static String fetchNextSkiptoken(JSONObject jsonObject) throws Exception {
		String skipToken = "";
		// Parse the skip token out of the string.
		skipToken = jsonObject.optJSONObject("responseMsg").optString("odata.nextLink");

		if (!skipToken.equalsIgnoreCase("")) {
			// Remove the unnecessary prefix from the skip token.
			int index = skipToken.indexOf("$skiptoken=") + (new String("$skiptoken=")).length();
			skipToken = skipToken.substring(index);
		}
		return skipToken;
	}

	/**
	 * @param jsonObject
	 * @return
	 * @throws Exception
	 */
	public static String fetchDeltaLink(JSONObject jsonObject) throws Exception {
		String deltaLink = "";
		// Parse the skip token out of the string.
		deltaLink = jsonObject.optJSONObject("responseMsg").optString("aad.deltaLink");
		if (deltaLink == null || deltaLink.length() == 0) {
			deltaLink = jsonObject.optJSONObject("responseMsg").optString("aad.nextLink");
			logger.info("deltaLink empty, nextLink ->" + deltaLink);

		}
		if (!deltaLink.equalsIgnoreCase("")) {
			// Remove the unnecessary prefix from the skip token.
			int index = deltaLink.indexOf("deltaLink=") + (new String("deltaLink=")).length();
			deltaLink = deltaLink.substring(index);
		}
		return deltaLink;
	}

	/**
	 * This method creates a string consisting of a JSON document with all
	 * the necessary elements set from the HttpServletRequest request.
	 *
	 * @param request
	 *            The HttpServletRequest
	 * @return The string containing the JSON document
	 * @throws Exception
	 *             If there is any error processing the request.
	 */
	public static String createJSONString(HttpServletRequest request, String controller) throws Exception {
		JSONObject obj = new JSONObject();
		try {
			Field[] allFields = Class.forName(
					"com.microsoft.windowsazure.activedirectory.sdk.graph.models." + controller).getDeclaredFields();
			String[] allFieldStr = new String[allFields.length];
			for (int i = 0; i < allFields.length; i++) {
				allFieldStr[i] = allFields[i].getName();
			}
			List<String> allFieldStringList = Arrays.asList(allFieldStr);
			Enumeration<String> fields = request.getParameterNames();

			while (fields.hasMoreElements()) {

				String fieldName = fields.nextElement();
				String param = request.getParameter(fieldName);
				if (allFieldStringList.contains(fieldName)) {
					if (param == null || param.length() == 0) {
						if (!fieldName.equalsIgnoreCase("password")) {
							obj.put(fieldName, JSONObject.NULL);
						}
					} else {
						if (fieldName.equalsIgnoreCase("password")) {
							obj.put("passwordProfile", new JSONObject("{\"password\": \"" + param + "\"}"));
						} else {
							obj.put(fieldName, param);
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

	/**
	 *
	 * @param key
	 * @param value
	 * @return string format of this JSON object
	 * @throws Exception
	 */
	public static String createJSONString(String key, String value) throws Exception {

		JSONObject obj = new JSONObject();
		try {
			obj.put(key, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return obj.toString();
	}

	/**
	 * This is a generic method that copies the simple attribute values from an
	 * argument jsonObject to an argument generic object.
	 *
	 * @param jsonObject
	 *            The jsonObject from where the attributes are to be copied.
	 * @param destObject
	 *            The object where the attributes should be copied to.
	 * @throws Exception
	 *             Throws an Exception when the operation is unsuccessful.
	 */
	public static <T> void convertJSONObjectToDirectoryObject(JSONObject jsonObject, T destObject) throws Exception {

		// Get the list of all the field names.
		Field[] fieldList = destObject.getClass().getDeclaredFields();

		// For all the declared field.
		for (int i = 0; i < fieldList.length; i++) {
			// If the field is of type String, that is
			// if it is a simple attribute.
			if (fieldList[i].getType().equals(String.class)) {
				// Invoke the corresponding set method of the destObject using
				// the argument taken from the jsonObject.
				destObject
						.getClass()
						.getMethod(String.format("set%s", WordUtils.capitalize(fieldList[i].getName())),
								new Class[] { String.class })
						.invoke(destObject, new Object[] { jsonObject.optString(fieldList[i].getName()) });
			}
		}
	}

	public static JSONArray joinJSONArrays(JSONArray a, JSONArray b) {
		JSONArray comb = new JSONArray();
		for (int i = 0; i < a.length(); i++) {
			comb.put(a.optJSONObject(i));
		}
		for (int i = 0; i < b.length(); i++) {
			comb.put(b.optJSONObject(i));
		}
		return comb;
	}

}
