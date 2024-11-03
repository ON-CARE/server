package com.example.config.auth;

import com.example.config.jwt.TokenProvider;
import com.example.config.redis.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final Authenticator authenticator;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();

        // 특정 경로에 대해 JWT 검사를 건너뛰도록 예외 설정
        if (servletPath.equals("/public/auth/signup")
                || servletPath.equals("/public/auth/login")
                || servletPath.equals("/api/facilities/nearby")) { // nearby 경로 예외 추가
            filterChain.doFilter(request, response);
            return;
        }

        log.info("CustomAuthorizationFilter.class / doFilterInternal :" + servletPath +  ": 엑세스 토큰을 검사");

        boolean nowCreated = false;
        String accessToken = tokenProvider.resolveToken(request);

        // 엑세스 토큰이 없을 경우 처리
        if (!StringUtils.hasText(accessToken)) {
            log.info("CustomAuthorizationFilter.class / doFilterInternal : 엑세스 토큰 없음");

            String refreshToken = tokenProvider.getRefreshToken(request);
            if (refreshToken != null) {
                boolean refreshTokenValid = tokenProvider.validateRefreshToken(refreshToken);
                if (refreshTokenValid) {
                    accessToken = tokenProvider.createNewAccessToken(refreshToken, response);
                    nowCreated = true;
                } else {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }
            } else {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }

        // 블랙리스트에 있으면 처리
        if (!nowCreated && redisUtil.hasKey(accessToken)) {
            log.info("CustomAuthorizationFilter.class / doFilterInternal : 블랙리스트에 등록된 엑세스 토큰");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        // access 토큰 유효성 검증
        String validatedAccessToken = tokenProvider.validateAccessToken(accessToken, request, response);
        if (validatedAccessToken != null) {
            Authentication authentication = tokenProvider.getAuthentication(validatedAccessToken);
            authenticator.setAuthenticationInSecurityContext(authentication);
        } else {
            log.info("CustomAuthorizationFilter.class / doFilterInternal : JWT access 토큰, refresh 토큰 모두 유효하지 않음");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        filterChain.doFilter(request, response); // 다음 필터 실행
    }
}
