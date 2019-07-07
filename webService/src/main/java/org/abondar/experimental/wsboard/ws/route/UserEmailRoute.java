package org.abondar.experimental.wsboard.ws.route;

import org.abondar.experimental.wsboard.dao.SecurityCodeDao;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Response;

/**
 * Route for invoking sending emails by user service events
 *
 * @author a.bondar
 */
public class UserEmailRoute extends RouteBuilder {

    @Autowired
    private SecurityCodeDao securityCodeDao;


    @Override
    public void configure() throws Exception {
        from("cxfrs:bean:jaxRsServer?bindingStyle=SimpleConsumer&performInvocation=true&synchronous=true")
                .routeId("userEmailRoute")
                .toD("direct:${headers.operationName}", false);

        from("direct:createUser").routeId("createUser")
                .log("${headers}")
                .transform()
                .body((bdy, hdrs) -> {

                    try {
                        var resp = (Response) bdy;
                        var usr = resp.readEntity(User.class);

                        hdrs.put("emailType", "createUser");
                        hdrs.put("code", securityCodeDao.insertCode(usr.getId()));
                        hdrs.put("To", usr.getEmail());
                        return usr;
                    } catch (ResponseProcessingException ex) {

                    } catch (DataExistenceException ex) {
                        bdy = Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
                    }

                    return bdy;
                })
                .log("${body}")
                .wireTap("direct:sendEmail");

        from("direct:updateLogin").routeId("updateLogin")
                .transform()
                .body((bdy, hdrs) -> {

                    try {
                        var resp = (Response) bdy;
                        var usr = resp.readEntity(User.class);

                        hdrs.put("emailType", "updateLogin");
                        hdrs.put("To", usr.getEmail());
                        return usr;
                    } catch (ResponseProcessingException ex) {

                    }

                    return bdy;
                })
                .log("${body}")
                .wireTap("direct:sendEmail");


        from("direct:resetPassword").routeId("resetPassword")
                .transform()
                .body((bdy, hdrs) -> {

                    try {
                        var resp = (Response) bdy;
                        var usr = resp.readEntity(User.class);

                        hdrs.put("emailType", "resetPassword");
                        hdrs.put("To", usr.getEmail());
                        return usr;
                    } catch (ResponseProcessingException ex) {

                    }

                    return bdy;
                })
                .log("${body}")
                .wireTap("direct:sendEmail");


        from("direct:updatePassword").routeId("updatePassword")
                .transform()
                .body((bdy, hdrs) -> {

                    try {
                        var resp = (Response) bdy;
                        var usr = resp.readEntity(User.class);

                        hdrs.put("emailType", "updatePassword");
                        hdrs.put("To", usr.getEmail());
                        return usr;
                    } catch (ResponseProcessingException ex) {

                    }

                    return bdy;
                })
                .log("${body}")
                .wireTap("direct:sendEmail");

        from("direct:deleteUser").routeId("deleteUser")
                .transform()
                .body((bdy, hdrs) -> {

                    try {
                        var resp = (Response) bdy;
                        var usr = resp.readEntity(User.class);

                        hdrs.put("emailType", "deleteUser");
                        hdrs.put("To", usr.getEmail());
                        return usr;
                    } catch (ResponseProcessingException ex) {

                    }

                    return bdy;
                })
                .log("${body}")
                .wireTap("direct:sendEmail");

    }
}
