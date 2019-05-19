package org.abondar.experimental.wsboard.webService.security;

import org.apache.cxf.rs.security.jose.jwt.JwtException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Class for checking if token is expired
 */
public class TokenExpiredMapper implements ExceptionMapper<JwtException> {

    @Override
    public Response toResponse(JwtException e) {
        return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }
}
