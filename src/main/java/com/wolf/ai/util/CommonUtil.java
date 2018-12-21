package com.wolf.ai.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import com.google.gson.Gson;

public class CommonUtil {
	public static String toJson(Object obj) {
		if (obj == null) {
			return null;
		}
		String json = new Gson().toJson(obj);
		return json;
	}

	public static <T> T fromJson(String jsonstr, Class<T> clazz) {
		if (StringUtils.isBlank(jsonstr)) {
			return null;
		}
		T fromJson = new Gson().fromJson(jsonstr, clazz);
		return fromJson;
	}

	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String image2base64(BufferedImage bimg) throws IOException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			ImageIO.write(bimg, "jpg", outputStream);
			return Base64.encodeBase64String(outputStream.toByteArray());
		}
	}

	public static BufferedImage base642image(String str) throws IOException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decodeBase64(str))) {
			return ImageIO.read(bais);
		}
	}
}
