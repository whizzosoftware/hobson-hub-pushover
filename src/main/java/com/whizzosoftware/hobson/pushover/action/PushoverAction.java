/*
 *******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************
*/
package com.whizzosoftware.hobson.pushover.action;

import com.whizzosoftware.hobson.api.action.ActionLifecycleContext;
import com.whizzosoftware.hobson.api.action.SingleAction;
import com.whizzosoftware.hobson.api.plugin.EventLoopExecutor;
import com.whizzosoftware.hobson.api.plugin.PluginContext;
import com.whizzosoftware.hobson.api.plugin.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;

/**
 * An action class that defines an action to send SMSs using Twilio.
 *
 * @author Dan Noguerol
 */
public class PushoverAction extends SingleAction {
    private final static Logger logger = LoggerFactory.getLogger(PushoverAction.class);

    private String userKey;
    private String apiToken;
    private String message;

    PushoverAction(PluginContext pctx, PushoverExecutionContext aectx, EventLoopExecutor executor) {
        super(pctx, aectx, executor);
        this.userKey = aectx.getUserKey();
        this.apiToken = aectx.getApiToken();
        this.message = (String)aectx.getProperties().get("message");
    }

    @Override
    public void onStart(ActionLifecycleContext ctx) {
        logger.info("Sending message to Pushover: " + message);

        try {
            StringBuilder data = new StringBuilder("token=")
                    .append(URLEncoder.encode(apiToken, "UTF8"))
                    .append("&user=")
                    .append(URLEncoder.encode(userKey, "UTF8"))
                    .append("&message=")
                    .append(URLEncoder.encode(message, "UTF8"));

            ((PushoverExecutionContext)getContext()).sendHttpRequest(
                new URI("https://api.pushover.net/1/messages.json"),
                HttpRequest.Method.POST,
                null,
                data.toString().getBytes()
            );

            ctx.complete();
        } catch (Exception e) {
            ctx.fail("Error sending Pushover message: " + e);
        }
    }

    @Override
    public void onMessage(ActionLifecycleContext ctx, String msgName, Object prop) {

    }

    @Override
    public void onStop(ActionLifecycleContext ctx) {

    }
}
