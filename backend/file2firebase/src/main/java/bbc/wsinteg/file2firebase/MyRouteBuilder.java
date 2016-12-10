package bbc.wsinteg.file2firebase;

import java.util.Map;
import java.util.HashMap;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;
import org.apache.camel.model.dataformat.JsonLibrary;
import bbc.wsinteg.fcm.Message;

import java.io.File;
import java.util.ArrayList;

public class MyRouteBuilder extends RouteBuilder {

    private static int max = 2060; // json message can't be more for notifications

    public void configure() {
	Processor myProcessor = new Processor() {
    		public void process(Exchange exchange) {
			Map<String,String> data = new HashMap<String,String>();
			data.put("filename", exchange.getIn().getHeader("CamelFileNameConsumed", String.class));
			data.put("body", exchange.getIn().getBody(String.class));
			data.put("part", exchange.getProperty("CamelSplitIndex", String.class));
			data.put("last", exchange.getProperty("CamelSplitComplete", Boolean.class)?"1":"0");
			Message m = Message.buildDataMessage("/topics/"+System.getProperty("topic"), data);
			exchange.getIn().setBody(m);
    		}
	};
        from("file:data")
		.marshal().zipFile()
		.to("file:zip")
		.marshal().base64()
		.to("file:enc")
		.split().tokenize("\n", 25).streaming()
		.to("log:split?showHeaders=true&showBody=false")
		.setHeader("CamelFileName",
			simple("${header.CamelFileNameConsumed}_${exchangeProperty.CamelSplitIndex}_${exchangeProperty.CamelSplitComplete}"))
		.to("file:enc")
		.process(myProcessor)
		.to("seda:send");
	from("seda:send")
		.throttle(1).timePeriodMillis(2000)
		.setHeader("Authorization", constant("key="+System.getProperty("apiKey")))
		.setHeader("Content-Type", constant("application/json"))
		.setHeader(Exchange.HTTP_METHOD, constant("POST"))
		.marshal().json(JsonLibrary.Jackson)
		//.to("log:send?showHeaders=true&showBody=true")
		.to("https://fcm.googleapis.com/fcm/send");
    }
}
