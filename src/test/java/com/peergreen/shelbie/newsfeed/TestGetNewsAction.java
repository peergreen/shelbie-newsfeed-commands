/**
 * Copyright 2013 Peergreen S.A.S.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.peergreen.shelbie.newsfeed;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Field;
import java.net.URL;

import org.apache.felix.service.command.CommandSession;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ow2.shelbie.testing.ActionContainer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.peergreen.newsfeed.Rss;
import com.peergreen.newsfeed.RssService;
import com.peergreen.newsfeed.RssServiceException;
import com.peergreen.newsfeed.RssServiceNotConnectedException;

/**
 * Test of the GetNews command.
 * @author Florent Benoit
 */
public class TestGetNewsAction {

    @Mock
    private CommandSession session;

    @Mock
    private RssService rssService;

    @Mock
    private Rss rss;

    private ActionContainer getNewsCommand;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        GetNewsAction getNewsAction = new GetNewsAction();
        getNewsCommand = new ActionContainer(getNewsAction);
        Field rssServiceField = GetNewsAction.class.getDeclaredField("rssService");
        rssServiceField.setAccessible(true);
        rssServiceField.set(getNewsAction, rssService);
    }

    @Test
    public void testNoConnection() throws Exception {
        // throw internet connection
        doThrow(RssServiceNotConnectedException.class).when(rssService).parse(any(URL.class));

        getNewsCommand.execute(session);
        assertTrue(getNewsCommand.getSystemOut().contains("N/A: No internet connection"));
    }



    @Test
    public void testError() throws Exception {
        // throw internet connection
        doThrow(RssServiceException.class).when(rssService).parse(any(URL.class));

        getNewsCommand.execute(session);
        assertTrue(getNewsCommand.getSystemOut().contains("Error while reading RSS stream"));
    }


    @Test
    public void testNoItemsRss() throws Exception {
        doReturn(rss).when(rssService).parse(any(URL.class));

        getNewsCommand.execute(session);
        System.out.println(getNewsCommand.getSystemOut());
        assertTrue(getNewsCommand.getSystemOut().contains("No news"));
    }

}
