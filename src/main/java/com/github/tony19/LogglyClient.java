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
package com.github.tony19;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit.RestAdapter;
import retrofit.mime.TypedString;

/**
 * Loggly client
 *
 * @author tony19@gmail.com
 */
public class LogglyClient implements ILogglyClient {
    private static final String API_URL = "http://logs-01.loggly.com/";
    private final ILogglyRestApi loggly;
    private final String token;

    /**
     * Creates a Loggly client
     * @param token Loggly customer token
     *              http://loggly.com/docs/customer-token-authentication-token/
     */
    public LogglyClient(@NotNull String token) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();

        this.token = token;
        this.loggly = restAdapter.create(ILogglyRestApi.class);
    }

    /**
     * Creates a Loggly client with the specified REST API.
     * This is package private for internal testing only.
     * @param token Loggly customer token
     * @param restApi implementation of {@link ILogglyRestApi}
     */
    LogglyClient(@NotNull String token, ILogglyRestApi restApi) {
        this.token = token;
        this.loggly = restApi;
    }

    /**
     * Posts a log message to Loggly
     * @param message message to be logged
     * @return {@code true} if successful; {@code false} otherwise
     */
    public boolean log(@NotNull String message) {
        if (message == null) return false;

        boolean ok;
        try {
            ok = loggly.log(token, new TypedString(message)).isOk();
        } catch (Exception e) {
            ok = false;
        }
        return ok;
    }

    /**
     * Posts several log messages in bulk to Loggly
     * @param messages messages to be logged
     * @return {@code true} if successful; {@code false} otherwise
     */
    public boolean logBulk(@NotNull String... messages) {
        if (messages == null) return false;

        StringBuilder b = new StringBuilder();
        for (String s : messages) {
            // Preserve new-lines in this event by replacing them
            // with "\r". Otherwise, they're processed as event
            // delimiters, resulting in unintentional multiple events.
            b.append(s.replaceAll("[\r\n]", "\r")).append('\n');
        }

        boolean ok;
        try {
            ok = loggly.logBulk(token, new TypedString(b.toString())).isOk();
        } catch (Exception e) {
            ok = false;
        }
        return ok;
    }

    /**
     * Runs a demo of the Loggly client
     * @param args command-line arguments. The first and only argument
     *             should be your Loggly customer token.
     * @throws IOException
     */
    public static void main(String... args) {
        final String TOKEN = System.getenv("LOGGLY_TOKEN");
        if (TOKEN == null || TOKEN.trim().isEmpty()) {
            System.err.println("LOGGLY_TOKEN env missing! See http://loggly.com/docs/customer-token-authentication-token/");
            System.exit(1);
        }

        final ILogglyClient loggly = new LogglyClient(TOKEN);
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
}