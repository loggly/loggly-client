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

import retrofit2.Call;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link com.github.tony19.loggly.LogglyClient}
 * @author tony19@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class LogglyClientTest {

    @Mock
    private ILogglyRestService restApi;

    private ILogglyClient loggly;
    private static final String TOKEN = "1e29e92a-b099-49c5-a260-4c56a71f7c89";
    private static final String NO_TAGS = null;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        restApi = mock(ILogglyRestService.class);
        loggly = new LogglyClient(TOKEN, restApi);

        Call<LogglyResponse> call = mock(Call.class);
        Mockito.doReturn(true).when(call).isExecuted();
        Mockito.doReturn(call).when(restApi).logBulk(anyString(), anyString(), anyString());
        Mockito.doReturn(call).when(restApi).log(anyString(), isNull(String.class), anyString());
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
        assertThat(loggly.logBulk((String[])null), is(false));
        Mockito.verifyZeroInteractions(restApi);
    }

    @Test
    public void logBulkVarargsRejectsNull() {
        assertThat(loggly.logBulk(null, ""), is(false));
        Mockito.verifyZeroInteractions(restApi);
    }

    @Test
    public void logCallsLogRestApi() {
        final String event = "hello world\nthis is a\nmulti-line event";
        assertThat(loggly.log(event), is(true));
        Mockito.verify(restApi).log(TOKEN, NO_TAGS, event);
    }

    @Test
    public void logBulkCallsBulkRestApi() {
        final boolean ok = loggly.logBulk("E 1", "E 2", "E 3");
        assertThat(ok, is(true));
        Mockito.verify(restApi).logBulk(TOKEN, NO_TAGS, "E 1\nE 2\nE 3\n");
    }

    @Test
    public void logBulkPreservesNewLineAsCarriageReturn() {
        final boolean ok = loggly.logBulk("multi-line\nevent here", "event 2");
        assertThat(ok, is(true));
        Mockito.verify(restApi).logBulk(TOKEN, NO_TAGS, "multi-line\revent here\nevent 2\n");
    }

    @Test
    public void singleTagIsSentToLoggly() {
        loggly.setTags("foo");
        loggly.logBulk("event");
        Mockito.verify(restApi).logBulk(TOKEN, "foo", "event\n");
    }

    @Test
    public void singleCsvTagIsSentToLoggly() {
        loggly.setTags("foo,bar");
        loggly.logBulk("event");
        Mockito.verify(restApi).logBulk(TOKEN, "foo,bar", "event\n");
    }

    @Test
    public void multipleTagsAreSentToLoggly() {
        loggly.setTags("foo", "bar");
        loggly.logBulk("event");
        Mockito.verify(restApi).logBulk(TOKEN, "foo,bar", "event\n");
    }

    @Test
    public void mixOfSingleTagAndMultipleTagsAreSentToLoggly() {
        loggly.setTags("foo", "bar", "baz,abc", "w,x  ,y  ,z,  ");
        loggly.logBulk("event");
        Mockito.verify(restApi).logBulk(TOKEN, "foo,bar,baz,abc,w,x,y,z", "event\n");
    }

    @Test
    public void emptyTagsResultInNoTags() {
        loggly.setTags("", "  ", " ,", ",  ,  ,,  ");
        loggly.logBulk("event");
        Mockito.verify(restApi).logBulk(TOKEN, NO_TAGS, "event\n");
    }
}
