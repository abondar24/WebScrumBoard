package org.abondar.experimental.wsboard.server.mapper;

import org.abondar.experimental.wsboard.server.exception.TokenExpiredException;

import org.abondar.experimental.wsboard.server.util.I18nKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Locale;

/**
 * Returns 406 in case of expired token
 *
 * @author a.bondar
 */
public class TokenExpiredMapper implements ExceptionMapper<TokenExpiredException> {


    private final MessageSource messageSource;

    @Autowired
    public TokenExpiredMapper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Response toResponse(TokenExpiredException e) {
        Locale locale = new Locale.Builder().setLanguage(e.getExLang()).build();
        return Response.status(Response.Status.NOT_ACCEPTABLE)
                .entity(messageSource.getMessage(I18nKeyUtil.SESSION_EXPIRED, null, locale)).build();
    }


}
