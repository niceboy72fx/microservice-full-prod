package com.acme.orders.rest;

import com.acme.orders.domain.Order;
import com.acme.orders.service.OrderService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderService orderService;

    @POST
    public Response createOrder(OrderRequest request) {
        Order order = orderService.createOrder(
            request.getCustomerId(),
            request.getAmount(),
            request.getCurrency()
        );
        return Response.status(Response.Status.CREATED).entity(order).build();
    }
}
