package com.ringme.config;

import com.google.gson.Gson;
import com.ringme.common.Helper;
import com.ringme.dto.record.Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    Gson gson;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("{} -> request: {}", request.getMethod(), request.getAttribute("jakarta.servlet.forward.request_uri"));

        Helper.setResponse(response, 500, gson.toJson(new Response(500, "Internal Server Error")));
    }
}