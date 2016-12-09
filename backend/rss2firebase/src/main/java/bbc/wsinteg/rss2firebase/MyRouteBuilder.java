package bbc.wsinteg.rss2firebase;

import java.util.Map;
import java.util.HashMap;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndContent;
import org.apache.camel.model.dataformat.JsonLibrary;
import bbc.wsinteg.fcm.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;

public class MyRouteBuilder extends RouteBuilder {

    private static int max = 2060; // json message can't be more for notifications

    public void configure() {
	Processor myProcessor = new Processor() {
    		public void process(Exchange exchange) {
			SyndFeed feed = exchange.getIn().getBody(SyndFeed.class);
			SyndEntry entry = (SyndEntry)feed.getEntries().get(0);

			Message m = new Message();
			m.to = "/topics/"+System.getProperty("topic");
			m.notification.title = entry.getTitle();
			m.notification.body = entry.getDescription().getValue();
			ObjectMapper mapper = new ObjectMapper();
			try {
				String jsonInString = mapper.writeValueAsString(m);
				int n = jsonInString.getBytes("UTF-8").length;
				if(n > max) {
					max -= 3; // leave room for an elipsis
					while(n > max) {
						m.notification.body = m.notification.body.substring(0, m.notification.body.length()-1); 
						jsonInString = mapper.writeValueAsString(m);
						n = jsonInString.getBytes("UTF-8").length;
					}
					m.notification.body += "\u2026";	
				}
			} catch(JsonProcessingException e) {
				e.printStackTrace();
			} catch(UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}
			exchange.getIn().setBody(m);
    		}
	};
        from("rss:"+System.getProperty("feed"))
		.removeHeader("CamelRssFeed")
		.process(myProcessor)
		.to("seda:send");
	from("seda:send")
		.setHeader("Authorization", constant("key="+System.getProperty("apiKey")))
		.setHeader("Content-Type", constant("application/json"))
		.setHeader(Exchange.HTTP_METHOD, constant("POST"))
		.marshal().json(JsonLibrary.Jackson)
		.to("log:send?showHeaders=true&showBody=true")
		.to("https://fcm.googleapis.com/fcm/send");
    }

}
 
