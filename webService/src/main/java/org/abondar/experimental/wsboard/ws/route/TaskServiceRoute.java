package org.abondar.experimental.wsboard.ws.route;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route for task service events
 *
 * @author a.bondar
 */
public class TaskServiceRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:createTask").routeId("createTask")
                .log("${headers}")
                .log("${body}");

        from("direct:updateTask").routeId("updateTask")
                .log("${headers}")
                .log("${body}");

        from("direct:updateTaskSprint").routeId("updateTaskSprint")
                .log("${headers}")
                .log("${body}");


        from("direct:updateTaskState").routeId("updateTaskState")
                .log("${headers}")
                .log("${body}");

        from("direct:deleteTask").routeId("deleteTask")
                .log("${body}");

        from("direct:getTaskById").routeId("getTaskById")
                .log("${headers}")
                .log("${body}");

        from("direct:getTasksForProject").routeId("getTasksForProject")
                .log("${headers}")
                .log("${body}");

        from("direct:getTasksForContributor").routeId("getTasksForContributor")
                .log("${headers}")
                .log("${body}");

        from("direct:getTasksForUser").routeId("getTasksForUser")
                .log("${headers}")
                .log("${body}");

        from("direct:getTasksForSprint").routeId("getTasksForSprint")
                .log("${headers}")
                .log("${body}");


    }
}
