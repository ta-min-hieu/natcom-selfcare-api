package com.ringme.dto.record;

public record Response(int code, String message, Object data) {
    public Response(int code, String message) {
        this(code, message, null);
    }
}
