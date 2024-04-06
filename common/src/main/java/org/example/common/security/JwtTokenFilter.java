package org.example.common.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.example.common.security.SecurityConst.HEADER_PREFIX;

/**
 * Фильтр аутентификации по JWT.
 * Если запрос попадает в этот фильтр, то запрос обязан содержать корректный JWT токен, иначе фильтр вернёт 401 статус
 */
@RequiredArgsConstructor
class JwtTokenFilter extends OncePerRequestFilter {

    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var jwt = request.getHeader(SecurityConst.HEADER_JWT);
        if (jwt == null || jwt.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        // парсинг токена
        JwtUserData userData;
        try {
            var key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            var data = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt.replace(HEADER_PREFIX, ""));
            var idStr = String.valueOf(data.getBody().get("id"));
            userData = new JwtUserData(
                    idStr == null ? null : UUID.fromString(idStr),
                    String.valueOf(data.getBody().get("login")),
                    String.valueOf(data.getBody().get("name"))
            );
        } catch (JwtException e) {
            // может случиться, если токен протух или некорректен
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        var authentication = new JwtAuthentication(userData);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
