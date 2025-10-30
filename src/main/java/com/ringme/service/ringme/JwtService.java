package com.ringme.service.ringme;

import com.ringme.dto.ringme.selfcare.UserInfo;
import com.ringme.enums.selfcare.SubType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Log4j2
public class JwtService {
    @Value("${jwtkey}")
    private String jwtkey;

    public String getUserNameFromHttpRequest(HttpServletRequest httpServletRequest) {
        String authorization = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String bearer = authorization.split(" ")[1].trim();

        return extractUsername(bearer);
    }

    // Generate token with given user name
    public String generateToken(String msisdn, Date expireTime) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, msisdn, expireTime);
    }

    public String generateToken(UserInfo userInfo) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 5);

        Map<String, Object> claims = new HashMap<>();
        claims.put("name", userInfo.getName());
        claims.put("avatar", userInfo.getAvatar());
        claims.put("subType", userInfo.getSubType() != null ? userInfo.getSubType().getType() : null);
        claims.put("language", userInfo.getLanguage());

        claims.put("subId", userInfo.getSubId());
        claims.put("vtAccId", userInfo.getVtAccId());

        return createToken(claims, userInfo.getIsdn(), cal.getTime());
    }

    // Create a JWT token with specified claims and subject (username)
    private String createToken(Map<String, Object> claims, String msisdn, Date expireTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(msisdn)
                .setIssuedAt(new Date())
                .setExpiration(expireTime)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Get the signing key for JWT token
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract the username from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate the token against user details and expiration
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String getUsernameFromJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public String extractSubId(HttpServletRequest request) {
        try {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            String bearerToken = authorization.split(" ")[1].trim();

            Claims claims = Jwts.parser()
                    .setSigningKey(getSignKey())
                    .parseClaimsJws(bearerToken)
                    .getBody();

            return String.valueOf(claims.get("subId", Long.class));
        } catch (Exception e) {
            log.error("error: {}", e.getMessage(), e);
        }
        return null;
    }

    public String extractName(HttpServletRequest request) {
        try {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            String bearerToken = authorization.split(" ")[1].trim();

            Claims claims = Jwts.parser()
                    .setSigningKey(getSignKey())
                    .parseClaimsJws(bearerToken)
                    .getBody();

            return String.valueOf(claims.get("name", String.class));
        } catch (Exception e) {
            log.error("error: {}", e.getMessage(), e);
        }
        return null;
    }

    public SubType extractSubType(HttpServletRequest request) {
        try {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            String bearerToken = authorization.split(" ")[1].trim();

            Claims claims = Jwts.parser()
                    .setSigningKey(getSignKey())
                    .parseClaimsJws(bearerToken)
                    .getBody();

            Integer subTypeId = claims.get("subType", Integer.class);
            if(subTypeId == null)
                return null;
            return SubType.fromCode(subTypeId);
        } catch (Exception e) {
            log.error("error: {}", e.getMessage(), e);
        }
        return null;
    }

    public String extractVtAccId(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        String bearerToken = authorization.split(" ")[1].trim();

        Claims claims = Jwts.parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(bearerToken)
                .getBody();

        return String.valueOf(claims.get("vtAccId", Long.class));
    }
}