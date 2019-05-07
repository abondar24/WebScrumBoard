package org.abondar.experimental.wsboard.webService.impl;

import org.abondar.experimental.wsboard.webService.service.ProjectService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/prohject")
public class ProjectServiceImpl implements ProjectService {

    @GET
    @Path("/echo")
    public void echo() {
    }
}
