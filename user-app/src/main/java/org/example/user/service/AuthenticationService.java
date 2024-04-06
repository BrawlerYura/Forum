package org.example.user.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.common.exceptions.UnauthorizedException;
import org.example.user.model.entity.UserEntity;
import org.example.common.security.props.SecurityProps;
import org.example.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static java.lang.System.currentTimeMillis;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final SecurityProps securityProps;

    public String generateJwt(UserEntity userEntity) throws UnauthorizedException {
        var user = userRepository.findByLogin(userEntity.getLogin());
        if (!(Objects.equals(userEntity.getPassword(), user.getPassword()))) {
            throw new UnauthorizedException("Incorrect password");
        }
        var key = Keys.hmacShaKeyFor(securityProps.getJwtToken().getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(user.getName())
                .setClaims(Map.of(
                        "login", user.getLogin(),
                        "id", user.getId().toString(),
                        "name", user.getName()
                ))
                .setId(UUID.randomUUID().toString())
                .setExpiration(new Date(currentTimeMillis() + securityProps.getJwtToken().getExpiration()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
