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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.HandlerDeclaration;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.service.command.CommandSession;
import org.fusesource.jansi.Ansi;

import com.peergreen.newsfeed.FeedMessage;
import com.peergreen.newsfeed.Rss;
import com.peergreen.newsfeed.RssService;
import com.peergreen.newsfeed.RssServiceException;
import com.peergreen.newsfeed.RssServiceNotConnectedException;

/**
 * Display the report of an artifact.
 */
@Component
@Command(name = "get-news",
         scope = "newsfeed",
         description = "Gets the latest Peergreen news.")
@HandlerDeclaration("<sh:command xmlns:sh='org.ow2.shelbie'/>")
public class GetNewsAction implements Action {

    public static String RSS_PEERGREEN = "http://www.peergreen.com/Blog/BlogRss?xpage=plain";

    private URL peergreenRssURL;

    @Requires
    private RssService rssService;

    public Object execute(final CommandSession session) throws Exception {

        Ansi buffer = Ansi.ansi();

       // Gets the latest news
        try {
            this.peergreenRssURL = new URL(RSS_PEERGREEN);
            buffer.render("@|bold,underline %s|@", "Peergreen News");
            buffer.newline();

            Rss rss = null;
            try {
                rss = rssService.parse(peergreenRssURL);
                Collection<FeedMessage> items = rss.getItems();
                if (items == null || items.size() == 0) {
                    System.out.println("No news");
                } else {
                    for (FeedMessage feedMessage : items) {
                        buffer.render("@|bold %s|@", feedMessage.getTitle());
                        buffer.a(" : ");
                        buffer.a(feedMessage.getLink());
                        buffer.newline();
                    }
                }
            } catch (RssServiceNotConnectedException e) {
                buffer.a("N/A: No internet connection");
            } catch (RssServiceException e) {
                buffer.a("Error while reading RSS stream");
                buffer.a(e);
            }

            System.out.println(buffer.toString());


        } catch (MalformedURLException e) {
           System.out.println("Unable to build URL of Peergreen RSS");
        }



        return null;
    }

}