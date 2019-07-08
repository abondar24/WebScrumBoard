package org.abondar.experimental.wsboard.ws.route;


import org.apache.camel.builder.RouteBuilder;

/**
 * Route for contributor service events
 *
 * @author a.bondar
 */
public class ContributorServiceRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("direct:createContributor").routeId("createContributor")
                .log("${headers}")
                .log("${body}");

        from("direct:updateContributor").routeId("updateContributor")
                .log("${headers}")
                .log("${body}");

        from("direct:findProjectOwner").routeId("findProjectOwner")
                .log("${headers}")
                .log("${body}");

        from("direct:findProjectContributors").routeId("findProjectContributors")
                .log("${headers}")
                .log("${body}");

    }
}
