package org.abondar.experimental.wsboard.server.service;


import org.abondar.experimental.wsboard.server.datamodel.user.User;
import org.abondar.experimental.wsboard.server.exception.CannotPerformOperationException;
import org.abondar.experimental.wsboard.server.exception.DataExistenceException;
import org.abondar.experimental.wsboard.server.exception.InvalidHashException;
import org.abondar.experimental.wsboard.server.exception.InvalidPasswordException;
import org.abondar.experimental.wsboard.server.mapper.DataMapper;
import org.abondar.experimental.wsboard.server.util.LogMessageUtil;
import org.abondar.experimental.wsboard.server.util.PasswordUtil;
import org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm;
import org.apache.cxf.rs.security.jose.jws.HmacJwsSignatureProvider;
import org.apache.cxf.rs.security.jose.jws.JwsCompactProducer;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactProducer;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * Authorization service implementation
 *
 * @author a.bondar
 */
@Component
public class AuthServiceImpl implements AuthService {
    private static final Long EXPIRY_PERIOD = 604800L;
    private static final String TOKEN_ISSUER = "borscht";

    private final DataMapper dataMapper;

    @Autowired
    public AuthServiceImpl(DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }

    @Override
    public String createToken(String login, String issuer, List<String> roles) {
        JwsHeaders headers = new JwsHeaders(SignatureAlgorithm.HS256);
        JwtClaims claims = new JwtClaims();
        claims.setSubject(login);
        claims.setIssuer(issuer);
        claims.setClaim("roles",roles);
        Calendar now = Calendar.getInstance();
        long issuedAt = now.getTimeInMillis() / 1000;
        claims.setIssuedAt(issuedAt);
        claims.setExpiryTime(issuedAt + EXPIRY_PERIOD);

        JwsCompactProducer jws = new JwsJwtCompactProducer(headers, claims);
        return jws.signWith(new HmacJwsSignatureProvider("Ym9yc2NodA", SignatureAlgorithm.HS256));
    }

    @Override
    public boolean validateUser(String login, String password) throws DataExistenceException {
        User user = dataMapper.getUserByLogin(login);
        if (user == null){
            throw new DataExistenceException(LogMessageUtil.USER_NOT_EXISTS);
        }

        if (password == null) {
            throw new InvalidPasswordException(LogMessageUtil.NULL_PASS);
        }
        if (user.getPassword().startsWith("sha1:64000")) {
            try {
                if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
                    throw new InvalidPasswordException(LogMessageUtil.VERIFICATION_FAILED);
                }
            } catch (CannotPerformOperationException | InvalidHashException ex) {
                throw new InvalidPasswordException(LogMessageUtil.VERIFICATION_FAILED, ex);
            }
        } else if (!user.getPassword().equals(password)) {
            throw new InvalidPasswordException(LogMessageUtil.VERIFICATION_FAILED);
        }

        return true;
    }

    @Override
    public String authorizeUser(String login, String pwd) throws DataExistenceException {
        if (validateUser(login, pwd)) {
            return createToken(login, TOKEN_ISSUER,List.of("*"));
        }
        return null;
    }



}
