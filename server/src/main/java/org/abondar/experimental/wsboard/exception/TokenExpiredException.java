package org.abondar.experimental.wsboard.exception;

import org.apache.cxf.rs.security.jose.jwt.JwtException;

/**
 * Exception thrown if token is expired
 *
 * @author a.bondar
 */
public class TokenExpiredException extends JwtException {

    private final String exLang;


    public TokenExpiredException(String message,String exLang){
        super(message);
        this.exLang = exLang;
    }

    public String getExLang() {
        return exLang;
    }
}
