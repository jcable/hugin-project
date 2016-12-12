package bbc.wsinteg.xmpp;

import org.apache.camel.builder.RouteBuilder;

public class MyRouteBuilder extends RouteBuilder {

    public void configure() {

	from("xmpp://{{host}}:{{port}}/?user={{user}}&password={{key}}&connectionConfig=#xmppcc")
	.to("log:receive?showHeaders=true&showBody=true");

    }
}
