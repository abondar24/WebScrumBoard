package org.abondar.experimental.wsboard.ws.impl;

import org.abondar.experimental.wsboard.ws.service.ProjectService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/prohject")
public class ProjectServiceImpl implements ProjectService {

    @GET
    @Path("/echo")
    public void echo() {
    }
}
