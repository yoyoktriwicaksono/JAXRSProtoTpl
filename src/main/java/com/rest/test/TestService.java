package com.rest.test;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Yoyok_T on 14/09/2018.
 */
@Path("/entry-point")
@Api(value = "/entry-point", description = "Web Services to browse entities")
public class TestService {
    @GET
    @Path("/test")
    @ApiOperation(value = "Return one entity", notes = "Returns one entity at random", response = Entity.class)
    @Produces({MediaType.APPLICATION_JSON})
    public Response test() {
//        return Response.status(200).entity("Test ").build();
        Entity entity = new Entity();
        entity.setName("entity0");
        return Response.ok().entity(entity).build();
    }
}
