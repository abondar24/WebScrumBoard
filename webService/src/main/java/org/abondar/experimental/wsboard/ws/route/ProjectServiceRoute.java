package org.abondar.experimental.wsboard.ws.route;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route for project service events
 *
 * @author a.bondar
 */
public class ProjectServiceRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:createProject").routeId("createProject")
                .log("${headers}")
                .log("${body}");

        from("direct:updateProject").routeId("updateProject")
                .log("${headers}")
                .log("${body}");

        from("direct:deleteProject").routeId("deleteProject")
                .log("${body}");

        from("direct:findProjectById").routeId("findProjectById")
                .log("${headers}")
                .log("${body}");


    }
}
