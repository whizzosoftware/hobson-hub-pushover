/*
 *******************************************************************************
 * Copyright (c) 2016 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************
*/
package com.whizzosoftware.hobson.pushover.action;

import com.whizzosoftware.hobson.api.action.Action;
import com.whizzosoftware.hobson.api.action.ActionProvider;
import com.whizzosoftware.hobson.api.plugin.http.HttpRequest;
import com.whizzosoftware.hobson.api.property.PropertyConstraintType;
import com.whizzosoftware.hobson.api.property.PropertyContainerClassContext;
import com.whizzosoftware.hobson.api.property.TypedProperty;
import com.whizzosoftware.hobson.pushover.PushoverPlugin;

import java.net.URI;
import java.util.Map;

public class PushoverActionProvider extends ActionProvider {
    private PushoverPlugin plugin;

    public PushoverActionProvider(PushoverPlugin plugin) {
        super(PropertyContainerClassContext.create(plugin.getContext(), "sendMessage"), "Send Pushover message", "Send a message to Pushover", true, 2000);

        this.plugin = plugin;

        addSupportedProperty(
            new TypedProperty.Builder("message", "Message", "The message to send", TypedProperty.Type.STRING).
                constraint(PropertyConstraintType.required, true).
                build()
        );
    }

    @Override
    public Action createAction(final Map<String, Object> properties) {
        return new PushoverAction(plugin.getContext(), new PushoverExecutionContext() {
            @Override
            public String getUserKey() {
                return plugin.getUserKey();
            }

            @Override
            public String getApiToken() {
                return plugin.getApiToken();
            }

            @Override
            public void sendHttpRequest(URI uri, HttpRequest.Method method, Map<String, String> headers, byte[] body) {
                plugin.sendHttpRequest(uri, method, headers, null, body, null);
            }

            @Override
            public Map<String, Object> getProperties() {
                return properties;
            }
        }, plugin.getEventLoopExecutor());
    }
}
