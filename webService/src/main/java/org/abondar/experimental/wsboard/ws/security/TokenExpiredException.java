package org.abondar.experimental.wsboard.ws.security;

import org.apache.cxf.rs.security.jose.jwt.JwtException;

/**
 * Exception thrown if token is expired
 *
 * @author a.bondar
 */
public class TokenExpiredException extends JwtException {

    private String exLang;


    public TokenExpiredException(String message,String exLang){
        super(message);
        this.exLang = exLang;
    }

    public String getExLang() {
        return exLang;
    }
}
