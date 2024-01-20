package com.hust.bookstore.filter;

import com.hust.bookstore.dto.CustomUserDetail;
import com.hust.bookstore.service.impl.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.hust.bookstore.common.Constants.TRACE_ID;
import static com.hust.bookstore.common.Constants.X_REQUEST_ID;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtServiceImpl jwtTokenProvider;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtServiceImpl jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        log.info("Filtering request {}.", request.getRequestURI());
        String traceId = request.getHeader(X_REQUEST_ID);
        MDC.put(TRACE_ID, org.apache.commons.lang3.StringUtils.isBlank(traceId) ? String.valueOf(System.nanoTime()) : traceId);

        try {

            final String jwt = getJwt(request);
            if (jwt == null) {
                log.info("No token found.");
                filterChain.doFilter(request, response);
                return;
            }

            final String username = jwtTokenProvider.getUserNameFromJWT(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("Authenticating request : {}.", request.getRequestURI());
                CustomUserDetail userDetails = (CustomUserDetail) userDetailsService.loadUserByUsername(username);
                log.info("User {} is authenticated.", userDetails.getUsername());
                if (jwtTokenProvider.validateToken(jwt)) {
                    log.info("Token is valid.");
                    final var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
        MDC.clear();
    }

    private String getJwt(HttpServletRequest request) {
        Optional<String> authHeader = Optional.ofNullable(request.getHeader("Authorization"));

        if (authHeader.isEmpty()) {
            return null;
        }

        String headerAuth = authHeader.get();

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
