<h1>loggly-client <a href='https://tony19.ci.cloudbees.com/job/loggly-client/'><a href='https://tony19.ci.cloudbees.com/job/loggly-client/job/loggly-client-SNAPSHOT/'><img src='https://tony19.ci.cloudbees.com/buildStatus/icon?job=loggly-client/loggly-client-SNAPSHOT'></a></a></h1>
<sup>v1.0.0</sup>

A Java client for posting log messages to [Loggly][1], a cloud logging service. This uses [Retrofit][2] to interface with Loggly's REST API.

Usage
-----

*Example:*

```java
final ILogglyClient loggly = new LogglyClient(/* LOGGLY_TOKEN */);
loggly.log("Hello!\nThis is a\nmulti-line event!\n");
loggly.logBulk("This is a\nmulti-line event 1",
               "Event 2",
               "Event 3");
```


Download
--------

loggly-client-1.0.0.jar (TBD)

_Gradle_:

```
compile 'com.github.tony19:loggly-client:1.0.0'
```

_Maven_:

```xml
<dependency>
  <groupId>com.github.tony19</groupId>
  <artifactId>loggly-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

[1]: http://loggly.com
[2]: http://square.github.io/retrofit/
