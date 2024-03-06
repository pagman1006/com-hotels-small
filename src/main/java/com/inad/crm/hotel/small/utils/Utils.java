package com.inad.crm.hotel.small.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

public class Utils {

	private Utils() { }

	public static boolean hasExcelFormat(MultipartFile file) {
		if (file == null || StringUtils.isBlank(file.getContentType())) {
			return false;
		}
		return Constants.TYPE.equals(file.getContentType());
	}

	public static String getLocalMessage(MessageSource messageSource, String key, String... params){
		return messageSource.getMessage(key,
				params, Locale.ENGLISH);
	}

	public static String removeAccents(String txt) {

		if (StringUtils.isBlank(txt)) {
			return null;
		}
		txt = txt.toUpperCase();
		return txt.replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U");
	}
}
