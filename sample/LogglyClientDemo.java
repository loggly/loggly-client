/**
 * Copyright (C) 2015 Anthony K. Trinh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.github.tony19.loggly.ILogglyClient;
import com.github.tony19.loggly.LogglyClient;

import java.util.Arrays;

/**
 * Demonstrates Loggly client usage
 */
public class LogglyClientDemo {

    /**
     * Runs a demo of the Loggly client
     *
     * @param args command-line arguments. The first and only argument
     *             should be your Loggly customer token.
     *             http://loggly.com/docs/customer-token-authentication-token/
     */
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
                        System.out.println("callback succeeded");
                    }

                    public void failure(String error) {
                        System.err.println("callback failed: " + error);
                    }
                });

        System.out.println("posting bulk events to Loggly asynchronously...");
        loggly.logBulk(Arrays.asList("E1", "E2"),
                new LogglyClient.Callback() {
                    public void success() {
                        System.out.println("bulk callback succeeded");
                    }

                    public void failure(String error) {
                        System.err.println("bulk callback failed: " + error);
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

        System.out.println("setting log tags to 'foo', 'bar', and 'baz'...");
        loggly.setTags("foo", "bar,baz");
        ok = loggly.log("This should be tagged with 'foo', 'bar', and 'baz'");
        System.out.println(ok ? "ok" : "err");
    }
}