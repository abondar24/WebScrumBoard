package org.abondar.experimental.wsboard.server.route;

import org.abondar.experimental.wsboard.server.util.I18nKeyUtil;
import org.abondar.experimental.wsboard.server.util.RouteConstantUtil;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

import java.util.Locale;



/**
 * Route sending email based on header
 *
 * @author a.bondar
 */
public class EmailRoute extends RouteBuilder {

    @Value("${email.server}")
    private String emailServer;

    @Value("${email.admin}")
    private String emailAdmin;

    @Value("${email.from}")
    private String emailFrom;


    private final MessageSource messageSource;

    private Locale locale;

    @Autowired
    public EmailRoute(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void configure() throws Exception {

        from("direct:sendEmail").routeId("sendEmail")
                .transform().body((bdy, hdrs) -> {
            hdrs.put("From", "Scrum Admin<" + emailAdmin + "@" + emailFrom + ">");
            hdrs.put("contentType", "text/html");

            locale = new Locale.Builder()
                    .setLanguage((String) hdrs.get("Accept-Language")).build();

            return bdy;
        })
                .choice()
                .when(header(RouteConstantUtil.EMAIL_TYPE_HEADER).isEqualTo("createUser"))
                .setHeader("head",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_HEAD, null, locale))
                .setHeader("body",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_CREATED_BODY, null, locale))
                .setHeader("body1",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_CREATED_BODY1, null, locale))
                .setHeader(RouteConstantUtil.THANKS_HEADER,()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_THANKS, null, locale))
                .to("velocity:/velocity/createUser.html")
                .removeHeader("email")
                .removeHeader("firstName")
                .removeHeader("lastName")
                .removeHeader(RouteConstantUtil.LOGIN_HEADER)
                .removeHeader("password")
                .removeHeader("roles")
                .endChoice()

                .when(header(RouteConstantUtil.EMAIL_TYPE_HEADER).isEqualTo("updateLogin"))
                .setHeader("head",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_HEAD, null, locale))
                .setHeader("body",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_UPDATE_LOGIN_BODY, null, locale))
                .setHeader("thanks",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_THANKS, null, locale))
                .to("velocity:/velocity/updateLogin.html")
                .removeHeader("firstName")
                .removeHeader("login")
                .removeHeader("id")
                .endChoice()

                .when(header(RouteConstantUtil.EMAIL_TYPE_HEADER).isEqualTo("updatePassword"))
                .setHeader("head",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_HEAD, null, locale))
                .setHeader("body",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_UPDATE_PWD_BODY, null, locale))
                .setHeader(RouteConstantUtil.THANKS_HEADER,()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_THANKS, null, locale))
                .to("velocity:/velocity/updatePassword.html")
                .removeHeader(RouteConstantUtil.LOGIN_HEADER)
                .removeHeader("id")
                .removeHeader("newPassword")
                .removeHeader("oldPassword")
                .endChoice()

                .when(header(RouteConstantUtil.EMAIL_TYPE_HEADER).isEqualTo("resetPassword"))
                .setHeader("head",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_HEAD, null, locale))
                .setHeader("body",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_RESET_PWD_BODY, null, locale))
                .setHeader("body1",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_RESET_PWD_BODY1, null, locale))
                .setHeader(RouteConstantUtil.THANKS_HEADER,()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_THANKS, null, locale))
                .to("velocity:/velocity/resetPassword.html")
                .removeHeader(RouteConstantUtil.LOGIN_HEADER)
                .removeHeader("id")
                .removeHeader("code")
                .endChoice()

                .when(header(RouteConstantUtil.EMAIL_TYPE_HEADER).isEqualTo("deleteUser"))
                .setHeader("head",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_HEAD_DELETE, null, locale))
                .setHeader("body",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_DELETE_BODY, null, locale))
                .setHeader("body1",()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_DELETE_BODY1, null, locale))
                .setHeader(RouteConstantUtil.THANKS_HEADER,()->
                        messageSource.getMessage(I18nKeyUtil.USER_EMAIL_THANKS, null, locale))
                .to("velocity:/velocity/deleteUser.html")
                .removeHeader("id")
                .end()
                .doTry()
                .to(emailServer)
                .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "${exception}")
                .endDoTry();
    }
}
