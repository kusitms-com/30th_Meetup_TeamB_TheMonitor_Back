package the_monitor.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import the_monitor.application.service.AccountService;
import the_monitor.domain.model.Account;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AccountService accountService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String authorizationHeader = request.getHeader("Authorization");
        String accessToken = null;

        // "Bearer " 접두어를 제거하고 실제 토큰 값만 가져오기
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);
        }

        if (accessToken != null && "VALID".equals(jwtProvider.validateToken(accessToken))) {
            // `accessToken`이 유효한 경우, 사용자 인증 정보를 설정
            Authentication authentication = jwtProvider.getAuthenticationFromToken(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (session != null) {
            // `refreshToken`을 세션에서 가져와 새 `accessToken` 발급
            String refreshToken = (String) session.getAttribute("refreshToken");
            if (refreshToken != null && "VALID".equals(jwtProvider.validateToken(refreshToken))) {
                // 사용자 정보 조회 (예: 세션이나 데이터베이스에서 `Account` 조회)
                Long accountId = jwtProvider.getAccountId(refreshToken);  // `refreshToken`의 클레임에서 ID 추출
                Account account = accountService.findAccountById(accountId);     // `accountService`를 통해 DB에서 조회

                // 새 `accessToken` 발급
                String newAccessToken = jwtProvider.generateAccessToken(account);

                // 새 `accessToken`을 응답 헤더에 설정하여 프론트엔드로 전달
                response.setHeader("Authorization", "Bearer " + newAccessToken);

                Authentication authentication = jwtProvider.getAuthenticationFromToken(newAccessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

}