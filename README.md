<h1>loggly-client <a href='https://tony19.ci.cloudbees.com/job/loggly-client/'><a href='https://tony19.ci.cloudbees.com/job/loggly-client/job/loggly-client-SNAPSHOT/'><img src='https://tony19.ci.cloudbees.com/buildStatus/icon?job=loggly-client/loggly-client-SNAPSHOT'></a></a></h1>
<sup>v1.0.1</sup>

A Java library for posting log messages to [Loggly][1], using [Retrofit][2] to interface with Loggly's REST API.

Usage
-----
1. Create a `LogglyClient` instance with your [authorization token][4] from Loggly.
 ```java
final String LOGGLY_TOKEN = /* your token */;
final ILogglyClient loggly = new LogglyClient(LOGGLY_TOKEN);
```

2. Log a single event...
 ```java
 loggly.log("Hellow world!");
 loggly.log("Ok to contain\nnew-lines in same event\n");
 loggly.log("Ok to contain\nnew-lines in same event\n",
              new LogglyClient.Callback() {
                 public void success() {
                     System.out.println("async log succeeded");
                 }
                 public void failure(String error) {
                     System.err.println("async log failed: " + error);
                 }
             });
 ```

 ...or several events in bulk.
 ```java
 loggly.logBulk("1st event",
                 "2nd event\nwith new-lines\n",
                 "3rd event");

Collection<String> events = Arrays.asList("4th event",
                                              "5th event\nwith new-lines\n");
loggly.logBulk(events);
loggly.logBulk(events,
        new LogglyClient.Callback() {
            public void success() {
                System.out.println("async bulk log succeeded");
            }
            public void failure(String error) {
                System.err.println("async bulk log failed: " + error);
            }
        });
 ```

 **Note:** In order to preserve event boundaries in a **bulk upload**, `loggly-client` replaces new-line characters (`\n'`) in an event with `'\r'`, which are subsequently stripped by Loggly. In the example above, the event is seen in Loggly as:
 ```
 2nd event with new-lines
 ```

Example
-------
```java
public static void main(String... args) {

    if (args.length == 0 || args[0].trim().isEmpty()) {
        System.err.println("missing argument: loggly token\nsee http://loggly.com/docs/customer-token-authentication-token/");
        System.exit(1);
    }

    final String TOKEN = args[0];
    final ILogglyClient loggly = new LogglyClient(TOKEN);

    System.out.println("posting single event to Loggly asynchronously...");
    loggly.log("Hello!\nThis is a\nmulti-line event!\n",
            new LogglyClient.Callback() {
                public void success() {
                    System.out.println("async log succeeded");
                }
                public void failure(String error) {
                    System.err.println("async log failed: " + error);
                }
            });

    System.out.println("posting bulk events to Loggly asynchronously...");
    loggly.logBulk(Arrays.asList("E1", "E2"),
            new LogglyClient.Callback() {
                public void success() {
                    System.out.println("async bulk log succeeded");
                }
                public void failure(String error) {
                    System.err.println("async bulk log failed: " + error);
                }
            });

    System.out.println("posting single event to Loggly...");
    boolean ok = loggly.log("Hello!\nThis is a\nmulti-line event!\n");
    System.out.println(ok ? "ok" : "err");

    System.out.println("posting single JSON event to Loggly...");
    final String json = "{ \"timestamp\": \"2015-01-01T12:34:00Z\", \"message\": \"Event 100\", \"count\": 100 }";
    ok = loggly.log(json);
    System.out.println(ok ? "ok" : "err");

    System.out.println("posting bulk events to Loggly...");
    ok = loggly.logBulk("This is a\nmulti-line event 1", "Event 2", "Event 3");
    System.out.println(ok ? "ok" : "err");
}
```

Download
--------

[loggly-client-1.0.1.jar][5]

#### Gradle

```
compile 'com.github.tony19:loggly-client:1.0.1'
```

#### Maven

```xml
<dependency>
  <groupId>com.github.tony19</groupId>
  <artifactId>loggly-client</artifactId>
  <version>1.0.1</version>
</dependency>
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][3].


[1]: http://loggly.com
[2]: http://square.github.io/retrofit/
[3]: https://oss.sonatype.org/content/repositories/snapshots/com/github/tony19/loggly-client/
[4]: https://www.loggly.com/docs/customer-token-authentication-token/
[5]: http://goo.gl/gLDVmu
