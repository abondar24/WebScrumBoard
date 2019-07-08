package org.abondar.experimental.wsboard.ws.route;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route for contributor route events
 *
 * @author a.bondar
 */
public class SprintServiceRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("direct:createSprint").routeId("createSprint")
                .log("${headers}")
                .log("${body}");

        from("direct:updateSprint").routeId("updateSprint")
                .log("${headers}")
                .log("${body}");

        from("direct:getSprintById").routeId("getSprintById")
                .log("${headers}")
                .log("${body}");

        from("direct:getSprints").routeId("getSprints")
                .log("${headers}")
                .log("${body}");

        from("direct:deleteSprint").routeId("deleteSprint")
                .log("${body}");
    }
}
