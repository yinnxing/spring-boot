package com.flora.spring_boot.service;

import com.flora.spring_boot.Application;
import com.flora.spring_boot.dto.request.AuthenticationRequest;
import com.flora.spring_boot.dto.request.IntrospectRequest;
import com.flora.spring_boot.dto.request.LogoutRequest;
import com.flora.spring_boot.dto.request.RefreshRequest;
import com.flora.spring_boot.dto.response.AuthenticationResponse;
import com.flora.spring_boot.dto.response.IntrospectResponse;
import com.flora.spring_boot.entity.InvalidedToken;
import com.flora.spring_boot.entity.User;
import com.flora.spring_boot.exception.AppException;
import com.flora.spring_boot.exception.ErrorCode;
import com.flora.spring_boot.repository.InvalidedTokenRepository;
import com.flora.spring_boot.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.text.ParseException;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidedTokenRepository invalidedTokenRepository;
    @NonFinal
    @Value("${jwt.valid.duration}")
    protected Long VALID_DURATION;
    @NonFinal
    @Value("${jwt.refreshable.duration}")
    protected Long REFRESHABLE_DURATION;
    @NonFinal
    @Value("${jwt.signer.key}")
    protected String SIGNER_KEY;

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedToken = verifyToken(request.getToken(), true);

        var jit = signedToken.getJWTClaimsSet().getJWTID();
        var expiryTime = signedToken.getJWTClaimsSet().getExpirationTime();

        InvalidedToken invalidedToken = InvalidedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidedTokenRepository.save(invalidedToken);

        String username = signedToken.getJWTClaimsSet().getSubject();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHENTICATED)
        );
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch(AppException e){
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public void Logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            InvalidedToken invalidedToken = InvalidedToken.builder()
                    .id(jit)
                    .expiryTime(signToken.getJWTClaimsSet().getExpirationTime())
                    .build();
            invalidedTokenRepository.save(invalidedToken);
        } catch (AppException e){
            log.info("Token has already expired");
        }


    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }
    private String generateToken(User user){
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("flora.com")
                    .issueTime(new Date())
                    .expirationTime(new Date(new Date().getTime() + 20000))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("scope", buildScope(user))
                    .build();
            JWSObject jwsObject = new JWSObject(
                    new JWSHeader(JWSAlgorithm.HS512),
                    new Payload(jwtClaimsSet.toJSONObject())
            );
           try{
               jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
               return jwsObject.serialize();
           } catch (JOSEException e){
               log.error("Cannot create token" + e);
               throw new RuntimeException(e);
           }
        }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expireTime = (isRefresh) ?
                Date.from(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plusSeconds(REFRESHABLE_DURATION))
               : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);
        if(!(verified && expireTime.after(new Date()))){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if(invalidedTokenRepository
                .existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }
        private String buildScope(User user){
            StringJoiner stringJoiner = new StringJoiner(" ");
            if(!CollectionUtils.isEmpty(user.getRoles())){
                user.getRoles().forEach(role -> {
                    stringJoiner.add("ROLE_" + role.getName());
                    if (!CollectionUtils.isEmpty(role.getPermissions()))
                        role.getPermissions()
                                .forEach(permission -> stringJoiner.add(permission.getName()));
                });
            }

            return stringJoiner.toString();
        }


}
