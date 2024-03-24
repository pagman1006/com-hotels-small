package com.inad.crm.hotel.small.utils;

import com.inad.crm.hotel.small.commons.enums.I18Constants;
import com.inad.crm.hotel.small.core.exception.exceptions.NoSuchElementsFoundException;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;

import java.util.List;

public class Validator {

	private Validator() { }

	public static void validatePage(Page<?> pageable, MessageSource messageSource) {
		if (pageable == null || pageable.getContent().isEmpty()) {
			throw new NoSuchElementsFoundException(Utils.getLocalMessage(messageSource, I18Constants.NO_ITEMS_FOUND.getKey()));
		}
	}

	public static void validateList(List<?> list, MessageSource messageSource) {
		if (list == null || list.isEmpty()) {
			throw new NoSuchElementsFoundException(Utils.getLocalMessage(messageSource, I18Constants.NO_ITEMS_FOUND.getKey()));
		}
	}
}
