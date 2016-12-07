package bbc.wsinteg.fcm;

public class Message {

    public Message() {
	this.to = "";
	this.notification = new Notification();
    }

    public String to;
    public Notification notification;
}
