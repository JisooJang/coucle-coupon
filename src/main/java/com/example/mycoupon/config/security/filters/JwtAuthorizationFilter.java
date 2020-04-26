package com.example.mycoupon.config.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.mycoupon.config.security.JWTSecurityConstants;
import com.example.mycoupon.exceptions.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

// validate requests containing JWTS
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    @Autowired
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(JWTSecurityConstants.HEADER_STRING);
        if (header == null || !header.startsWith(JWTSecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if(authentication != null) {
            req.setAttribute("memberId", Long.parseLong((String)authentication.getPrincipal()));
        }
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws IOException {
        // get JWT from headers and validate token.
        String token = request.getHeader(JWTSecurityConstants.HEADER_STRING);
        if(token != null) {
            String user = null;
            try {
                user = JWT.require(Algorithm.HMAC512(JWTSecurityConstants.SECRET.getBytes()))
                        .build()
                        .verify(token.replace(JWTSecurityConstants.TOKEN_PREFIX, ""))
                        //.getSubject();
                        .getAudience().get(0);
            } catch(JWTDecodeException | InvalidClaimException e) {
//                response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                PrintWriter writer = response.getWriter();
//                writer.println("Invalid Authorization Token.");
                throw new InvalidTokenException(e, HttpStatus.UNAUTHORIZED.value());
            }

            if(user != null) {
                // 인증이 완료된 토큰 return
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }

//    @Override
//    protected void onUnsuccessfulAuthentication(HttpServletRequest request,
//                                                HttpServletResponse response, AuthenticationException failed) throws IOException {
//
//        System.out.println(request);
//        System.out.println(response);
//    }
}
