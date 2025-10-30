package com.ringme.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class LocaleFactory {
    @Autowired
    MessageSource messageSource;

    private static final Map<String, Locale> locales = Map.of(
            "en", Locale.ENGLISH,
            "ht", new Locale("ht")
    );

    public Locale getLocale(String lang) {
        if (lang == null || lang.isBlank()) {
            return Locale.ENGLISH;
        }
        return locales.getOrDefault(lang.toLowerCase(), Locale.ENGLISH);
    }

    public String getMessage(String key, String language) {
        return messageSource.getMessage(key, null, key, getLocale(language));
    }

    public String getMessage(String key) {
        return messageSource.getMessage(key, null, key, getLocale("en"));
    }
}
