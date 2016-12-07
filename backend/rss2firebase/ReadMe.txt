Apache Camel application to poll an RSS feed and sent notifications via Google Firebase
=======================================================================================

To build this project use

    mvn install

To run this project from within Maven use

    mvn exec:java -Dfeed=<RSS Feed URL> -DapiKey=<Your Firebase Server Key> -Dtopic=<FCM topic to send to>

For more help see the Apache Camel documentation

    http://camel.apache.org/

