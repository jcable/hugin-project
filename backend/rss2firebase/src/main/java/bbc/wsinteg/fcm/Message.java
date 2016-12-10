package bbc.wsinteg.fcm;

import java.util.Map;

public class Message {

    public static Message buildNotification(String to, String title, String body) {
	Message m = new Message();
	m.to = to;
	m.notification = new Notification();
	m.notification.title = title;
	m.notification.body = body;
	return m;
    }

    public static Message buildDataMessage(String to, Map<String, String> data) {
	Message m = new Message();
	m.to = to;
	m.data = data;
	return m;
    }

    public String to;
    public Notification notification;
    public Map<String,String> data;
}
