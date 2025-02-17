package com.example.LibDev.auth.jwt;

import com.example.LibDev.auth.dto.TokenResDto;
import com.example.LibDev.global.service.RedisTokenService;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final UserDetailsService userDetailsService;
    private final RedisTokenService redisTokenService;

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    @Getter
    @Value("${spring.jwt.access-token-valid-time}")
    private long accessTokenValidTime;

    @Getter
    @Value("${spring.jwt.refresh-token-valid-time}")
    private long refreshTokenValidTime;

    private static Key signingkey;

    @PostConstruct
    public void init(){
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        signingkey = Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(Authentication authentication, String subject, long expiration) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put("role", authentication.getAuthorities().stream().findFirst().get().getAuthority());

        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","HS256")
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(signingkey,SignatureAlgorithm.HS256)
                .compact();

    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(signingkey).parseClaimsJws(token).getBody();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getClaimsFromToken(token).getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public boolean isValidToken(String token) {
        try{
            if(isBlacklisted(token)){
                return false;
            }
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException e) {
            return false;
        }
    }

    public long getTokenValidTime(String token){
        return getClaimsFromToken(token).getExpiration().getTime();
    }

    private boolean isBlacklisted(String token){
        return redisTokenService.hasKey(token);
    }
}
