package org.abondar.experimental.wsboard.ws.impl;

import org.abondar.experimental.wsboard.dao.data.DataMapper;
import org.abondar.experimental.wsboard.dao.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.dao.exception.InvalidHashException;
import org.abondar.experimental.wsboard.dao.exception.InvalidPasswordException;
import org.abondar.experimental.wsboard.dao.password.PasswordUtil;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.abondar.experimental.wsboard.ws.service.AuthService;
import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm;
import org.apache.cxf.rs.security.jose.jws.HmacJwsSignatureProvider;
import org.apache.cxf.rs.security.jose.jws.JwsCompactProducer;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactConsumer;
import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactProducer;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AuthServiceImpl implements AuthService {
    @Autowired
    private DataMapper dataMapper;

    private String secret = "borscht";

    private static final Long EXPIRY_PERIOD = 3600l;


    @Override
    public String renewToken(String tokenStr) throws Base64Exception {
        JwsJwtCompactConsumer jwtConsumer = new JwsJwtCompactConsumer(tokenStr);
        JwtToken token = jwtConsumer.getJwtToken();
        long issuedAt = (new Date()).getTime() / 1000;
        token.getClaims().setIssuedAt(issuedAt);
        token.getClaims().setExpiryTime(issuedAt + EXPIRY_PERIOD);
        JwsCompactProducer jws = new JwsJwtCompactProducer(token);
        jws.getJwsHeaders().setSignatureAlgorithm(SignatureAlgorithm.HS256);
        byte[] key = Base64Utility.decode(secret);
        jws.signWith(new HmacJwsSignatureProvider(key, SignatureAlgorithm.HS256));
        return jws.getSignedEncodedJws();

    }

    @Override
    public String getSubject(String token) {
        JwsJwtCompactConsumer jwtConsumer = new JwsJwtCompactConsumer(token);
        return jwtConsumer.getJwtClaims().getSubject();
    }

    @Override
    public String createToken(String login, String issuer, List<String> roles) {
        JwsHeaders headers = new JwsHeaders(SignatureAlgorithm.HS256);
        JwtClaims claims = new JwtClaims();
        claims.setSubject(login);
        claims.setIssuer(issuer);
        claims.setAudiences(roles);
        Calendar now = Calendar.getInstance();
        long issuedAt = now.getTimeInMillis() / 1000;
        claims.setIssuedAt(issuedAt);
        claims.setExpiryTime(issuedAt + EXPIRY_PERIOD);

        JwsCompactProducer jws = new JwsJwtCompactProducer(headers, claims);
        return jws.signWith(new HmacJwsSignatureProvider(secret.getBytes(), SignatureAlgorithm.HS256));
    }

    @Override
    public boolean validateUser(Long userId, String password) {
        User user = dataMapper.getUserById(userId);

        if (password == null) {
            throw new InvalidPasswordException("Supplied password is null!");
        }
        if (user.getPassword().startsWith("sha1:64000")) {
            try {
                if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
                    throw new InvalidPasswordException("Password verification failed!");
                }
            } catch (CannotPerformOperationException | InvalidHashException ex) {
                throw new InvalidPasswordException("Password verification failed!", ex);
            }
        } else if (!user.getPassword().equals(password)) {
            throw new InvalidPasswordException("Password verification failed!");
        }

        return true;
    }

    @Override
    public String authorizeUser(User user, String pwd) {
        if (validateUser(user.getId(), pwd)) {
            return createToken(user.getLogin(), "borscht", null);
        }
        return null;
    }

    @Override
    public void logRecord(Date logDate, String action, String subject, String record) {

    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
