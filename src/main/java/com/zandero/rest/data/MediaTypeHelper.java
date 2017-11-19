package com.zandero.rest.data;

import com.zandero.utils.Assert;
import com.zandero.utils.Pair;
import com.zandero.utils.StringUtils;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class MediaTypeHelper {

	private MediaTypeHelper() {
		// hide constructor
	}

	public static String getKey(MediaType mediaType) {

		if (mediaType == null) {
			return MediaType.WILDCARD;
		}

		return mediaType.getType() + "/" + mediaType.getSubtype(); // key does not contain any charset
	}

	public static MediaType valueOf(String mediaType) {

		try {
			return parse(mediaType);
		}
		catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Simple parsing utility to get MediaType
	 *
	 * @param mediaType to be parsed
	 * @return media type of throw IllegalArgumentException if parsing fails
	 */
	static MediaType parse(String mediaType) {

		Assert.notNullOrEmptyTrimmed(mediaType, "Missing media type!");

		String type = null;
		String subType = null;
		Map<String, String> params = new HashMap<>();

		String[] parts = mediaType.split(";");
		for (int i = 0; i < parts.length; i++) {

			String part = StringUtils.trimToNull(parts[i]);
			if (part == null) {
				continue;
			}

			if (i == 0) {
				// get type and subtype
				String[] typeSubType = part.split("/");
				Assert.isTrue(typeSubType.length == 2, "Missing or invalid type with subType: '" + part + "'");

				type = StringUtils.trimToNull(typeSubType[0]);
				subType = StringUtils.trimToNull(typeSubType[1]);

				Assert.notNull(type, "Missing media type!");
				Assert.notNull(subType, "Missing media sub-type!");

			} else {

				Pair<String, String> pair = getNameValue(part);
				params.put(pair.getKey(), pair.getValue());
			}
		}

		return new MediaType(type, subType, params);
	}

	private static Pair<String, String> getNameValue(String part) {

		String[] nameValue = part.split("=");
		Assert.isTrue(nameValue.length == 2, "Invalid param format: '" + part + "', expected name=value!");

		String name = StringUtils.trimToNull(nameValue[0]);
		String value = StringUtils.trimToNull(nameValue[1]);

		Assert.notNull(name, "Missing param name: '" + part + "'!");
		Assert.notNull(value, "Missing param value: '" + part + "'!");

		return new Pair<>(StringUtils.trim(nameValue[0]), nameValue[1]);
	}

	public static String toString(MediaType produces) {

		StringBuilder builder = new StringBuilder();
		builder.append(produces.getType()).append("/").append(produces.getSubtype());

		if (produces.getParameters().size() > 0) {
			for (String key: produces.getParameters().keySet()) {
				builder.append(";");
				builder.append(key).append("=").append(produces.getParameters().get(key));
			}
		}

		return builder.toString();
	}
}
