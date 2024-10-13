package the_monitor.infrastructure.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import the_monitor.application.service.AccountService;
import the_monitor.common.ErrorStatus;
import the_monitor.domain.model.Account;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (isPublicUrl(request.getRequestURI())) {
            log.info("공개 URL 접근: {}", request.getRequestURI());

            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = resolveTokenFromCookie(request);  // 쿠키에서 JWT 토큰 추출

        if (accessToken == null) {
            request.setAttribute("exception", ErrorStatus._JWT_NOT_FOUND);
            filterChain.doFilter(request, response);
            return;
        }

        switch (jwtProvider.validateToken(accessToken)) {
            case "VALID":
                Authentication authentication = jwtProvider.getAuthentication(accessToken);

                SecurityContextHolder.getContext().setAuthentication(authentication);  // 인증 설정
                log.info("===================== LOGIN SUCCESS");
                break;

            case "INVALID":
                log.info("===================== INVALID ACCESS-TOKEN");
                request.setAttribute("exception", ErrorStatus._JWT_INVALID);
                break;

            case "EXPIRED":
                log.info("===================== EXPIRED ACCESS-TOKEN");
                request.setAttribute("exception", ErrorStatus._JWT_EXPIRED);
                break;

        }

        filterChain.doFilter(request, response);

    }

    // 쿠키에서 토큰을 추출하는 메서드
    private String resolveTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isPublicUrl(String requestUrl) {
        return requestUrl.equals("/api") ||
                requestUrl.equals("/api/v1/accounts") ||
                requestUrl.equals("/api/v1/accounts/sendEmailConfirm") ||
                requestUrl.equals("/api/v1/accounts/verifyCode") ||
                requestUrl.equals("/api/v1/accounts/signUp") ||
                requestUrl.equals("/api/v1/accounts/signIn") ||
                requestUrl.startsWith("/api/kindergartens/**") ||
                requestUrl.startsWith("/swagger-ui/**") ||
                requestUrl.startsWith("/swagger-resources/**") ||
                requestUrl.startsWith("/v3/api-docs/**") ||
                requestUrl.startsWith("/favicon.ico");
    }

}