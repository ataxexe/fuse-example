package com.example.fuse.camel;

import com.example.fuse.model.Order;
import com.example.fuse.model.OrderRequest;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class InputRoute extends RouteBuilder {

  @Autowired
  private Environment environment;

  @Override
  public void configure() {
    restConfiguration()
        .bindingMode(RestBindingMode.json)
        .dataFormatProperty("prettyPrint", "true")
        .enableCORS(true)
        .port(environment.getProperty("api.port", "8080"))
        .contextPath("/api");

    rest("/orders").description("Orders")
        .consumes("application/json")
        .produces("application/json")

        .post().description("Places an order")
          .type(OrderRequest.class)
          .outType(Order.class)
          .to("direct:place-order");

    from("direct:place-order")
        .id("order-place")
        .streamCaching()
        .to("bean:orderService?method=place")
        .wireTap("activemq:orders")
        .to("mock:result");

    from("activemq:responses")
        .id("callback")
        .log("Received response for order ${body.order.id}, approved: ${body.approved}");

    from("activemq:orders")
        .id("web-service")
        // fancy stuff going on here
        .log("Sending order ${body.id} to web service")
        .delay(5000)
        .log("sending response regarding order ${body.id}")
        .to("bean:responseCreator?method=createResponse")
        .to("activemq:responses");

  }

}
