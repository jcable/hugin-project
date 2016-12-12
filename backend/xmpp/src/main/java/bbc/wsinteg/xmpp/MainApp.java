package bbc.wsinteg.xmpp;

import org.apache.camel.main.Main;
import org.apache.camel.component.properties.PropertiesComponent;
import org.jivesoftware.smack.ConnectionConfiguration;
import javax.net.ssl.SSLSocketFactory;

public class MainApp {

    public static void main(String... args) throws Exception {
        Main main = new Main();
        MyRouteBuilder routebuilder = new MyRouteBuilder();
        main.addRouteBuilder(routebuilder);

	PropertiesComponent pc = new PropertiesComponent();
	pc.setLocation("file:///etc/camel/xmpp.properties");

	// make properties available to the routebuilder
	main.bind("properties", pc);
	routebuilder.getContext().addComponent("properties", pc);

	// ConnectionConfiguration needs a host:port but this is also needed in the routebuilder
	String host = routebuilder.getContext().resolvePropertyPlaceholders("{{host}}");
	int port = Integer.parseInt(routebuilder.getContext().resolvePropertyPlaceholders("{{port}}"));

	// TLS config for xmpp
	ConnectionConfiguration connConfig = new ConnectionConfiguration(host, port);
	connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
        SSLSocketFactory sslFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
	connConfig.setSocketFactory(sslFactory);
	main.bind("xmppcc", connConfig);

        main.run(args);
    }

}

