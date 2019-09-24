package org.abondar.experimental.wsboard.ws.security;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.apache.cxf.rs.security.jose.jwt.JwtException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Returns 406 in case of expired token
 *
 * @author a.bondar
 */
public class TokenExpiredMapper implements ExceptionMapper<JwtException> {

    @Override
    public Response toResponse(JwtException e) {
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity(LogMessageUtil.SESSION_EXPIRED).build();
    }
}
