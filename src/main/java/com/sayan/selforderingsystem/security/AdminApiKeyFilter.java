package com.sayan.selforderingsystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@AllArgsConstructor
@Slf4j
public class AdminApiKeyFilter extends OncePerRequestFilter {

    private final String ADMIN_API_KEY;
    private static final String ADMIN_API_KEY_HEADER = "X-ADMIN-API-KEY";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Logger logger = LoggerFactory.getLogger(AdminApiKeyFilter.class);

        String apiKey = request.getHeader(ADMIN_API_KEY_HEADER);
        logger.info("Received API Key: {}", apiKey);  // Log received API key

        if (apiKey != null && apiKey.equals(ADMIN_API_KEY)) {
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
            var authentication = new UsernamePasswordAuthenticationToken(null, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Admin authentication successful");
        } else {
            logger.warn("Invalid API Key or missing");
        }

        filterChain.doFilter(request, response);
    }

}
