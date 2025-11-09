package com.Man10h.book_store.util;

import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Set<String> WHITE_LIST = new HashSet<>();
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    static {
        WHITE_LIST.add("/api/v1/home/**");
        WHITE_LIST.add("/api/v1/home");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            if(shouldNotFilter(request)){
                filterChain.doFilter(request, response);
                return;
            }
            String authHeader = request.getHeader("Authorization");
            if(authHeader == null || !authHeader.startsWith("Bearer ")){
                filterChain.doFilter(request, response);
                return;
            }
            String token = authHeader.substring(7);
            String username = tokenService.getUsername(token);
            if(SecurityContextHolder.getContext().getAuthentication() == null && username != null){
                UserEntity userEntity = (UserEntity) userDetailsService.loadUserByUsername(username);
                if(tokenService.validateToken(token)){
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, userEntity.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return WHITE_LIST.contains(request.getRequestURI());
    }
}

