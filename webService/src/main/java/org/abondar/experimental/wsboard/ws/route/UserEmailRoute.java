package org.abondar.experimental.wsboard.ws.route;

import org.abondar.experimental.wsboard.dao.SecurityCodeDao;
import org.abondar.experimental.wsboard.dao.exception.DataExistenceException;
import org.abondar.experimental.wsboard.datamodel.user.User;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

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
        from("cxfrs:bean:jaxRsServer?bindingStyle=SimpleConsumer")
                .routeId("userEmailRoute")
                .log(simple("${header.operationName}").toString())
                .toD("direct:${header.operationName}");

        from("direct:createUser").routeId("createUser")
                .transform()
                .body((bdy, hdrs) -> {

                    try {
                        hdrs.put("emailType", "createUser");
                        var usr = (User) bdy;
                        hdrs.put("code", securityCodeDao.insertCode(usr.getId()));

                    } catch (DataExistenceException ex) {
                        throw new RuntimeException(ex.getMessage());
                    }
                    return bdy;
                })
                .log("${body}")
                .to("direct:sendEmail");

        from("direct:updateLogin").routeId("updateLogin")
                .transform()
                .body((bdy, hdrs) -> hdrs.put("emailType", "updateLogin"))
                .to("direct:sendEmail");

        from("direct:resetPassword").routeId("resetPassword")
                .transform()
                .body((bdy, hdrs) -> hdrs.put("emailType", "resetPassword"))
                .to("direct:sendEmail");


        from("direct:updatePassword").routeId("updatePassword")
                .transform()
                .body((bdy, hdrs) -> hdrs.put("emailType", "updatePassword"))
                .to("direct:sendEmail");

        from("direct:deleteUser").routeId("deleteUser")
                .transform()
                .body((bdy, hdrs) -> hdrs.put("emailType", "deleteUser"))
                .to("direct:sendEmail");

    }
}
