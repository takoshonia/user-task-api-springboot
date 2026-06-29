package com.tamar.user_task_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;

/**
 * Sets locale early in the request so security filters and GlobalExceptionHandler
 * see the same language as controllers (from Accept-Language header).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LocaleContextFilter extends OncePerRequestFilter {

    private final LocaleResolver localeResolver;

    public LocaleContextFilter(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        LocaleContextHolder.setLocale(localeResolver.resolveLocale(request));
        try {
            filterChain.doFilter(request, response);
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }
}
