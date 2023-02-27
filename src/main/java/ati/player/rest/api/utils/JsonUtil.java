package ati.player.rest.api.utils;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonUtil {
	public static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();

	public static <T> String objectToJson(T object) {
		return GSON.toJson(object);
	}

	public static Map<String, String> jsonToMap(String json) {
		Map<String, String> jsonMap = GSON.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
		return jsonMap;
	}
	
	public static <T> String objectToJson(T object, Type type) {
		return GSON.toJson(object, type);
	}

	public static Object jsonToObject(String json, Type type) {
		return GSON.fromJson(json, type);
	}
	
}
