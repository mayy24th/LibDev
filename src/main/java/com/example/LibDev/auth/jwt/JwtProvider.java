package com.example.LibDev.auth.jwt;

import com.example.LibDev.auth.dto.TokenResDto;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
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

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.jwt.access-token-valid-time}")
    private long accessTokenValidTime;

    @Value("${spring.jwt.refresh-token-valid-time}")
    private long refreshTokenValidTime;

    private static Key signingkey;

    @PostConstruct
    public void init(){
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        signingkey = Keys.hmacShaKeyFor(bytes);
    }

    public TokenResDto generateToken(Authentication authentication) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put("role", authentication.getAuthorities().stream().findFirst().get().getAuthority());

        Date now = new Date();

        String accessToken = Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","HS256")
                .setSubject("access-token")
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .signWith(signingkey, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","HS256")
                .setSubject("refresh-token")
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+refreshTokenValidTime))
                .signWith(signingkey,SignatureAlgorithm.HS256)
                .compact();

        return new TokenResDto(accessToken, refreshToken);
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
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException e) {
            return false;
        }
    }

    public long getAccessTokenValidTime() {
        return accessTokenValidTime;
    }

    public long getRefreshTokenValidTime() {
        return refreshTokenValidTime;
    }

    public long getTokenValidTime(String token){
        return getClaimsFromToken(token).getExpiration().getTime();
    }
}
