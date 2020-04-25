package com.example.mycoupon.config.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.mycoupon.payload.UserModel;
import com.example.mycoupon.config.security.JWTSecurityConstants;
import com.example.mycoupon.config.security.SecurityMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

// issue JWTS to users sending credentials ("/login")

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    @Autowired
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        UserModel model = null;
        try {
            model = new ObjectMapper().readValue(req.getInputStream(), UserModel.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Authenticate user
        return authenticationManager.authenticate(
                // Create login token
                new UsernamePasswordAuthenticationToken(
                        model.getId(),
                        model.getPassword(),
                        Collections.emptyList()
                )
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        // setting JWT at response header
        SecurityMember member = (SecurityMember)auth.getPrincipal();

        long current = System.currentTimeMillis();
        Date issuedAt = new Date(current);
        Date expiredAt = new Date(current + JWTSecurityConstants.EXPIRATION_TIME);

        String token = JWT.create()
                .withIssuer("MyCoupon")
                .withAudience(Long.toString(member.getId()))
                .withSubject(member.getUsername())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiredAt)
                .sign(Algorithm.HMAC512(JWTSecurityConstants.SECRET.getBytes()));

        res.addHeader(JWTSecurityConstants.HEADER_STRING, JWTSecurityConstants.TOKEN_PREFIX + token);
    }
}
