package com.wastereborn.security;

import com.wastereborn.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserService userService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("🔐 JWT Filter: " + method + " " + requestURI);

        // Special debugging for admin endpoints
        if (requestURI.startsWith("/api/admin")) {
            System.out.println("🔧 ADMIN ENDPOINT DETECTED: " + requestURI);
        }

        // Debug all headers
        String authHeader = request.getHeader("Authorization");
        System.out.println("🔍 Authorization header: " + authHeader);

        String token = getTokenFromRequest(request);

        if (token != null) {
            System.out.println("🔑 Token found: " + token.substring(0, Math.min(20, token.length())) + "...");

            if (tokenProvider.validateToken(token)) {
                String email = tokenProvider.getEmailFromToken(token);
                System.out.println("✅ Valid token for user: " + email);

                UserDetails userDetails = userService.loadUserByUsername(email);
                System.out.println("🔍 User authorities: " + userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("✅ Authentication set for user: " + email + " with authorities: " + userDetails.getAuthorities());
            } else {
                System.err.println("❌ Invalid JWT token");
            }
        } else {
            System.out.println("⚠️ No JWT token found in request");
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
