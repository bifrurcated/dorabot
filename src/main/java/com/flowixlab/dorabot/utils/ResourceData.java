package com.flowixlab.dorabot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ResourceData {
    @Autowired
    private MessageSource messageSource;

    public String message(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }

    public String message(String code, Object ... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }

    public String message(String code, Locale locale, Object[] args) {
        return messageSource.getMessage(code, args, locale);
    }
}
