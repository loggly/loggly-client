<h1>loggly-client <a href='https://tony19.ci.cloudbees.com/job/loggly-client/'><a href='https://tony19.ci.cloudbees.com/job/loggly-client/job/loggly-client-SNAPSHOT/'><img src='https://tony19.ci.cloudbees.com/buildStatus/icon?job=loggly-client/loggly-client-SNAPSHOT'></a></a></h1>
<sup>v1.0.3</sup>

A Java library for posting log messages to [Loggly][1], using [Retrofit][2] to interface with Loggly's REST API.


Quickstart
----------
1. Create a `LogglyClient` instance with your [authorization token][4] from Loggly.
 ```java
final String LOGGLY_TOKEN = /* your token */;
final ILogglyClient loggly = new LogglyClient(LOGGLY_TOKEN);
```

2. Log an event...
 ```java
 loggly.log("Hello world!");
```



API
---

#### `setTags(String... tags)`

Sets the Loggly tag(s) with variable arity strings (or with a CSV) to be applied to all subsequent log calls. Specify empty string to clear all tags.

```java
loggly.setTags("foo", "bar");
// or equivalently:
loggly.setTags("foo,bar");
```

#### `log(String message)`

Logs a single event

```java
loggly.log("hello world!");
```


#### `log(String message, Callback callback)`

Logs an event **asynchronously**

```java
loggly.log("hello",
        new LogglyClient.Callback() {
            @Override
            public void success() {
                System.out.println("ok");
            }

            @Override
            public void failure(String error) {
                System.err.println("error: " + error);
            }
        });
 ```

#### `logBulk(String... messages)`
**Note:** In order to preserve event boundaries in a **bulk upload**, `loggly-client` replaces new-line characters (`'\n'`) with carriage-returns (`'\r'`), which are subsequently stripped by Loggly.

Logs multiple events in bulk with variable arity strings

 ```java
 loggly.logBulk("event 1", "event 2");
 ```


#### `logBulk(Collection<String> messages)`

Logs multiple events in bulk with a `Collection<String>`

 ```java
Collection<String> events = Arrays.asList("event 1", "event 2");
loggly.logBulk(events);
```


#### `logBulk(Collection<String> messages, Callback callback)`

Logs multiple events **asynchronously** in bulk with a `Collection<String>`

```java
Collection<String> events = Arrays.asList("event 1", "event 2");
loggly.logBulk(events,
        new LogglyClient.Callback() {
            @Override
            public void success() {
                System.out.println("ok");
            }

            @Override
            public void failure(String error) {
                System.err.println("error: " + error);
            }
        });
 ```


Download
--------

[loggly-client-1.0.3.jar][5]

#### Gradle

```
compile 'com.github.tony19:loggly-client:1.0.3'
```

#### Maven

```xml
<dependency>
  <groupId>com.github.tony19</groupId>
  <artifactId>loggly-client</artifactId>
  <version>1.0.3</version>
</dependency>
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][3].


[1]: http://loggly.com
[2]: http://square.github.io/retrofit/
[3]: https://oss.sonatype.org/content/repositories/snapshots/com/github/tony19/loggly-client/
[4]: https://www.loggly.com/docs/customer-token-authentication-token/
[5]: http://goo.gl/l3ScQv
