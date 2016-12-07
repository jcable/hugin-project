package wsinteg;

import java.util.Map;
import java.util.HashMap;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndContent;
import org.apache.camel.model.dataformat.JsonLibrary;
import wsinteg.fcm.Message;

/**
 * A Camel Java DSL Router
curl -v -X POST -H "Authorization: key=$api_key" -H 'Content-Type: application/json' 'https://fcm":"/topics/news","notification":{"title":"Hi","body":"there"}}'

 */
public class MyRouteBuilder extends RouteBuilder {

    //String server_key = "AAAAxu7HP60:APA91bHozcTBnmapUL-BFbjQqCtGNuu3xfNDMwRjbygrStEL5jIZM-kRMsYqi-_YQXUoMTklt9A2VWlRjD1ePSBkOaF9MyEy4_FSnP6wRn-KZkWNcok7fd9w2ovD1u9IZNpJME6rZ5dwC7Ew7-wgCqZDviY2Osi0Lg";
    //String feed = "http://feeds.bbci.co.uk/news/world/rss.xml?edition=uk"

    public void configure() {
	Processor myProcessor = new Processor() {
    		public void process(Exchange exchange) {
			SyndFeed feed = exchange.getIn().getBody(SyndFeed.class);
			SyndEntry entry = (SyndEntry)feed.getEntries().get(0);

			Message m = new Message();
			m.to = "/topics/"+System.getProperty("topic");
			m.notification.title = entry.getTitle();
			m.notification.body = entry.getDescription().getValue();

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
 
