package com.spring.task.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.task.service.TokenService;
import com.spring.task.service.serviceimpl.UserDetailsServiceImpl;
import com.spring.task.util.JwtUtils;
import com.spring.task.web.ApiError;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter to process JWT authentication for incoming requests.
 *
 * This filter extracts JWT token from the Authorization header, validates it, and sets the
 * authentication in the security context if the token is valid.
 *
 * If the token is expired, malformed, unsupported, or invalid, appropriate error responses
 * are sent back to the client.
 *
 * Created By Rahil Sathwara on 19-03-2024
 * @project product-management
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, JwtException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            try {
                if (tokenService.findByToken(token).isPresent()) {
                    username = jwtUtils.extractUsername(token);

                    if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
                        if(jwtUtils.validateToken(token, userDetails)){
                            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        }
                    }
                } else {
                    logger.error("JWT token not exist in token table");
                    this.onError(request, response, "JWT Token", "Invalid token", HttpStatus.UNAUTHORIZED);
                    return;
                }
            } catch (ExpiredJwtException e) {
                logger.error("JWT token expired: {}");
                this.onError(request, response, "JWT Token", "JWT token expired", HttpStatus.UNAUTHORIZED);
                return;
            } catch (MalformedJwtException e) {
                this.onError(request, response, "JWT Token", "Invalid JWT token", HttpStatus.FORBIDDEN);
                return;
            } catch (UnsupportedJwtException e) {
                this.onError(request, response, "JWT Token", "JWT token is unsupported", HttpStatus.FORBIDDEN);
                return;
            } catch (IllegalArgumentException e) {
                this.onError(request, response, "JWT Token","JWT token is expired", HttpStatus.FORBIDDEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void onError(HttpServletRequest request, HttpServletResponse response, String msg, String err, HttpStatus httpStatus) throws IOException {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (PrintWriter out = response.getWriter()) {
            List<String> details = new ArrayList<>();
            details.add(err);
            ApiError errData = new ApiError(LocalDateTime.now(), httpStatus, msg, details);
            String jsonData = objectMapper.writeValueAsString(errData);
            out.println(jsonData);
        } catch (JsonProcessingException e) {
            logger.error("An unexpected error occurred: {}");
        }
    }
}