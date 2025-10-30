package com.ringme.config;

import com.google.gson.Gson;
import com.ringme.common.Helper;
import com.ringme.dto.record.Response;
import com.ringme.service.ringme.JwtService;
import com.ringme.service.ringme.UserInfoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Log4j2
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoService userDetailsService;

    @Autowired
    Gson gson;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            String token;
            String username;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtService.extractUsername(token);
            } else {
                log.error("Bearer token not found: {} -> request: {}", request.getMethod(), request.getRequestURI());
                Helper.setResponse(response, HttpServletResponse.SC_UNAUTHORIZED, gson.toJson(new Response(401, "Bearer token not found")));
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails.getUsername(),
                            userDetails.getPassword(),
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.error("Bearer token invalid: {} -> request: {}", request.getMethod(), request.getRequestURI());
                    Helper.setResponse(response, HttpServletResponse.SC_UNAUTHORIZED, gson.toJson(new Response(401, "Bearer token invalid")));
                    return;
                }
            }
        } catch (Exception e) {
            log.error("Bearer token error: {} -> request: {}|ERROR|{}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
            Helper.setResponse(response, HttpServletResponse.SC_UNAUTHORIZED, gson.toJson(new Response(401, "Bearer token error")));
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        if (path.startsWith("/without-bearer/") ||
            path.startsWith("/clear-cache/") ||
            path.startsWith("/natcash/top-up/airtime/call-back") ||
            path.startsWith("/natcash/ftth/call-back") ||
            path.startsWith("/natcash/payment-mobile-service-vas/call-back") ||
            path.startsWith("/natcash/share-plan/call-back") ||
            path.startsWith("/natcash/test/cancel-trans-v2") ||
            path.startsWith("/actuator/")) {

            log.debug(path + " shouldNotFilter: true");
            return true;
        }
        return false;
    }
}
