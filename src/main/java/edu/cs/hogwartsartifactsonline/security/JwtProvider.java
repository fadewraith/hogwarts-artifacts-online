package edu.cs.hogwartsartifactsonline.security;

import edu.cs.hogwartsartifactsonline.hogwartsuser.MyUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    private final JwtEncoder jwtEncoder;

    public JwtProvider(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String createToken(Authentication authentication) {

        Instant now = Instant.now();
        long expiresIn = 2; // 2 hours

//        prepare a claim called authorities.
        String authorities = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.joining(" "));// must be space delimited. e.g. "ROLE_admin ROLE_user"

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expiresIn, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("userId", ((MyUserPrincipal)(authentication.getPrincipal())).getHogwartsUser().getId())
                .claim("authorities", authorities)
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }
}

/**
 * claims -
 * {
 *     "iss": "self",
 *     "sub": "john",
 *     "exp": 16789623216,
 *     "iat": 1678455454,
 *     "authorities": "ROLE_admin ROLE_user"
 * }
 * */
