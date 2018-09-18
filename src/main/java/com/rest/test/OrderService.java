package com.rest.test;

import com.rest.order.data.OrderData;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.wink.providers.protobuf.WinkProtobufProvider;
import org.glassfish.jersey.model.internal.RankedComparator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Yoyok_T on 14/09/2018.
 */
@Path("/order")
@Api(value = "/order", description = "Web Services to browse entities")
public class OrderService {
    @GET
    @Path("/get")
    @ApiOperation(value = "Return one entity", notes = "Returns one entity at random", response = OrderData.Order.class)
    @Produces({MediaType.APPLICATION_JSON})
//    @Produces({WinkProtobufProvider.PROTOBUF, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public OrderData.Order getOrder() {
        return OrderData.Order.newBuilder().setApplication("Vanila").build();
    }
}
