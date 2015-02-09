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

import java.io.Serializable;

/**
 * The response from Loggly's REST endpoints, which is a
 * JSON object, containing only a "response" key and its
 * value (normally equal to "ok" for success).
 *
 * @author tony19@gmail.com
 */
class LogglyResponse implements Serializable {

    /** Response value for success */
    public static final String SUCCESS_VALUE = "ok";

    /** A {@code LogglyResponse} that indicates success */
    public static final LogglyResponse OK = new LogglyResponse(SUCCESS_VALUE);

    /** Value of response. Assigned by deserializer. */
    private String response;

    /**
     * Constructs a blank {@code LogglyResponse}
     */
    public LogglyResponse() {
    }

    /**
     * Constructs a {@code LogglyResponse} for internal testing
     * @param text desired text value of response
     */
    LogglyResponse(String text) {
        this.response = text;
    }

    /**
     * Gets the text value of the response
     * @return the response as a string
     */
    public String getText() {
        return response;
    }

    /**
     * Determines whether the response indicates success
     * @return {@code true} if the result is success; {@code false} otherwise
     */
    public boolean isOk() {
        return SUCCESS_VALUE.equals(response);
    }
}
