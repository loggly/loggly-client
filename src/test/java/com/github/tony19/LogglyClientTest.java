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

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import retrofit.mime.TypedString;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

/**
 * Tests {@link com.github.tony19.LogglyClient}
 * @author tony19@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class LogglyClientTest {

    private ILogglyClient loggly;
    private @Mock ILogglyRestApi restApi;
    private static final String TOKEN = "1e29e92a-b099-49c5-a260-4c56a71f7c89";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        restApi = Mockito.mock(ILogglyRestApi.class);
        loggly = new LogglyClient(TOKEN, restApi);
    }

    @Test
    public void constructorRejectsNull() {
        exception.expect(IllegalArgumentException.class);
        new LogglyClient(null);
    }

    @Test
    public void logRejectsNull() {
        assertThat(loggly.log(null), is(false));
        Mockito.verifyZeroInteractions(restApi);
    }

    @Test
    public void logBulkRejectsNull() {
        assertThat(loggly.logBulk(null), is(false));
        Mockito.verifyZeroInteractions(restApi);
    }

    @Test
    public void logCallsLogRestApi() {
        Mockito.doReturn(LogglyResponse.OK).when(restApi).log(anyString(), any(TypedString.class));
        final String event = "hello world\nthis is a\nmulti-line event";
        assertThat(loggly.log(event), is(true));
        Mockito.verify(restApi).log(TOKEN, new TypedString(event));
    }

    @Test
    public void logBulkCallsBulkRestApi() {
        Mockito.doReturn(LogglyResponse.OK).when(restApi).logBulk(anyString(), any(TypedString.class));
        final boolean ok = loggly.logBulk("E 1", "E 2", "E 3");
        assertThat(ok, is(true));
        Mockito.verify(restApi).logBulk(TOKEN, new TypedString("E 1\nE 2\nE 3\n"));
    }

    @Test
    public void logBulkPreservesNewLineAsCarriageReturn() {
        Mockito.doReturn(LogglyResponse.OK).when(restApi).logBulk(anyString(), any(TypedString.class));
        final boolean ok = loggly.logBulk("multi-line\nevent here", "event 2");
        assertThat(ok, is(true));
        Mockito.verify(restApi).logBulk(TOKEN, new TypedString("multi-line\revent here\nevent 2\n"));
    }
}
