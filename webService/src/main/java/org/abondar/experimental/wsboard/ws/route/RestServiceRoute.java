package org.abondar.experimental.wsboard.ws.route;

import org.apache.camel.builder.RouteBuilder;

/**
 * Basic rest service route
 *
 * @author a.bondar
 */
public class RestServiceRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("cxfrs:bean:jaxRsServer?bindingStyle=SimpleConsumer&performInvocation=true&synchronous=true")
                .routeId("restServiceRoute")
                .toD("direct:${headers.operationName}", false);

    }
}
