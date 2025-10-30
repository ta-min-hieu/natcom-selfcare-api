package com.ringme.config;

import com.ringme.enums.CodeEnum;
import com.ringme.enums.selfcare.SubType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CodeEnumConverterFactory implements ConverterFactory<String, CodeEnum> {

    @Override
    public <T extends CodeEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return new CodeEnumConverter<>(targetType);
    }

    private static class CodeEnumConverter<T extends CodeEnum> implements Converter<String, T> {
        private final Class<T> enumType;

        public CodeEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            if (source == null || source.isBlank()) return null;

            Object code;
            try {
                code = Integer.parseInt(source);
            } catch (NumberFormatException e) {
                try {
                    code = Long.parseLong(source);
                } catch (NumberFormatException ex) {
                    code = source.trim(); // fallback: dùng string
                }
            }

            for (T constant : enumType.getEnumConstants()) {
                if (Objects.equals(constant.getType(), code)) {
                    return constant;
                }
            }

            throw new IllegalArgumentException("Cannot convert '" + source + "' to enum " + enumType.getSimpleName());
        }
    }
}