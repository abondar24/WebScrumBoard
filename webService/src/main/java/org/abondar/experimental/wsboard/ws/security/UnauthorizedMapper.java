package org.abondar.experimental.wsboard.ws.security;

import org.abondar.experimental.wsboard.dao.data.LogMessageUtil;
import org.apache.cxf.rs.security.jose.common.JoseException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Returns 401 if token is not set
 *
 * @author a.bondar
 */
public class UnauthorizedMapper implements ExceptionMapper<JoseException> {
    @Override
    public Response toResponse(JoseException e) {
        return Response.status(Response.Status.UNAUTHORIZED).entity(LogMessageUtil.JWT_TOKEN_NOT_SET).build();
    }
}
