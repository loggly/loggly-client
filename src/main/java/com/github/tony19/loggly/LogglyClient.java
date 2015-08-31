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
package com.github.tony19.loggly;

import java.util.Arrays;
import java.util.Collection;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

/**
 * Loggly client
 *
 * @author tony19@gmail.com
 */
public class LogglyClient implements ILogglyClient {
    private static final String API_URL = "http://logs-01.loggly.com/";
    private ILogglyRestService loggly;
    private final String token;
    private String tags;

    /**
     * Creates a Loggly client
     * @param token Loggly customer token
     * http://loggly.com/docs/customer-token-authentication-token/tag/android
     */
    public LogglyClient(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("token cannot be empty");
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();

        this.token = token;
        this.loggly = restAdapter.create(ILogglyRestService.class);
    }

    

    /**
     * Creates a Loggly client with the specified REST API.
     * This is package private for internal testing only.
     * @param token Loggly customer token
     * @param restApi implementation of {@link ILogglyRestService}
     */
    LogglyClient(String token, ILogglyRestService restApi) {
        this.token = token;
        this.loggly = restApi;
    }

    /**
     * Sets the tags to use for Loggly messages. The list of
     * strings are converted into a single CSV (trailing/leading
     * spaces stripped from each entry).
     * @param tags CSV or list of tags
     */
    public void setTags(String... tags) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String s : tags) {
            for (String t : s.split(",")) {
                 t = t.trim();
                 if (!t.isEmpty()) {
                     if (!first) {
                         builder.append(",");
                     }
                     builder.append(t);
                 }
                 first = false;
            }
        }
        // "tags" field must be null for Retrofit to exclude Loggly tags header.
        // Empty header string is not acceptable.
        this.tags = builder.length() > 0 ? builder.toString() : null;
    }

    /**
     * Posts a log message to Loggly
     * @param message message to be logged
     * @return {@code true} if successful; {@code false} otherwise
     */
    public boolean log(String message) {
        if (message == null) return false;

        boolean ok;
        try {
            ok = loggly.log(token, tags, new TypedString(message)).isOk();
        } catch (Exception e) {
            e.printStackTrace();
            ok = false;
        }
        return ok;
    }

    /**
     * Posts a log message asynchronously to Loggly
     * @param message message to be logged
     * @param callback callback to be invoked on completion of the post
     */
    public void log(String message, final Callback callback) {
        if (message == null) return;

        loggly.log(token,
                tags,
                new TypedString(message),
                new retrofit.Callback<LogglyResponse>() {
                    public void success(LogglyResponse logglyResponse, Response response) {
                        callback.success();
                    }

                    public void failure(RetrofitError retrofitError) {
                        callback.failure(retrofitError.getMessage());
                    }
                });
    }

    /**
     * Posts several log messages in bulk to Loggly
     * @param messages messages to be logged
     * @return {@code true} if successful; {@code false} otherwise
     */
    public boolean logBulk(String... messages) {
        if (messages == null) return false;
        return logBulk(Arrays.asList(messages));
    }

    /**
     * Posts several log messages in bulk to Loggly
     * @param messages messages to be logged
     * @return {@code true} if successful; {@code false} otherwise
     */
    public boolean logBulk(Collection<String> messages) {
        if (messages == null) return false;

        String parcel = joinStrings(messages);
        if (parcel.isEmpty()) return false;

        boolean ok;
        try {
            ok = loggly.logBulk(token, tags, new TypedString(parcel)).isOk();
        } catch (Exception e) {
            e.printStackTrace();
            ok = false;
        }
        return ok;
    }

    /**
     * Posts several log messages in bulk to Loggly asynchronously
     * @param messages messages to be logged
     * @param callback callback to be invoked on completion of the post
     */
    public void logBulk(Collection<String> messages, final Callback callback) {
        if (messages == null) return;

        String parcel = joinStrings(messages);
        if (parcel.isEmpty()) return;

        loggly.logBulk(token,
                tags,
                new TypedString(parcel),
                new retrofit.Callback<LogglyResponse>() {
                    public void success(LogglyResponse logglyResponse, Response response) {
                        callback.success();
                    }

                    public void failure(RetrofitError retrofitError) {
                        callback.failure(retrofitError.getMessage());
                    }
                });
    }

    /**
     * Combines a collection of messages to be sent to Loggly.
     * In order to preserve event boundaries, the new lines in
     * each message are replaced with '\r', which get stripped
     * by Loggly.
     * @param messages messages to be combined
     * @return a single string containing all the messages
     */
    private String joinStrings(Collection<String> messages) {
        StringBuilder b = new StringBuilder();
        for (String s : messages) {
            if (s == null || s.isEmpty()) {
                continue;
            }

            // Preserve new-lines in this event by replacing them
            // with "\r". Otherwise, they're processed as event
            // delimiters, resulting in unintentional multiple events.
            b.append(s.replaceAll("[\r\n]", "\r")).append('\n');
        }
        return b.toString();
    }
}
