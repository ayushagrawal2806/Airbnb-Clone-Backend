package com.airbnb.AirbnbClone.Filter;

import com.airbnb.AirbnbClone.entity.User;
import com.airbnb.AirbnbClone.security.JWTService;
import com.airbnb.AirbnbClone.service.implementation.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")){
                filterChain.doFilter(request , response);
                return;
            }

            String token = requestTokenHeader.substring(7);
            Long userId = jwtService.getUserIdFromToken(token);

            if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null){
                User user = userService.getUserById(userId);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(user , null , user.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                filterChain.doFilter(request , response);
            }
        } catch (Exception ex){
            handlerExceptionResolver.resolveException(request,response,null,ex);
        }
    }
}
