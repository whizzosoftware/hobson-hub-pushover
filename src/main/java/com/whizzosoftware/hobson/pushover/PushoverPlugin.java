/*
 *******************************************************************************
 * Copyright (c) 2015 Whizzo Software, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************
*/
package com.whizzosoftware.hobson.pushover;

import com.whizzosoftware.hobson.api.plugin.PluginStatus;
import com.whizzosoftware.hobson.api.plugin.http.AbstractHttpClientPlugin;
import com.whizzosoftware.hobson.api.plugin.http.HttpResponse;
import com.whizzosoftware.hobson.api.property.PropertyConstraintType;
import com.whizzosoftware.hobson.api.property.PropertyContainer;
import com.whizzosoftware.hobson.api.property.TypedProperty;
import com.whizzosoftware.hobson.pushover.action.PushoverActionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A plugin that publishes a task action for sending SMSs via Twilio.
 *
 * @author Dan Noguerol
 */
public class PushoverPlugin extends AbstractHttpClientPlugin {
    private static final Logger logger = LoggerFactory.getLogger(PushoverPlugin.class);

    private String userKey;
    private String apiToken;
    private boolean actionProviderPublished = false;

    public PushoverPlugin(String pluginId, String version, String description) {
        super(pluginId, version, description);
    }

    @Override
    protected TypedProperty[] getConfigurationPropertyTypes() {
        return new TypedProperty[] {
            new TypedProperty.Builder("userKey", "User Key", "The user key generated for you by Pushover", TypedProperty.Type.STRING).
                    constraint(PropertyConstraintType.required, true).
                    build(),
            new TypedProperty.Builder("apiToken", "API Token/Key", "The API token for the application you set up in Pushover", TypedProperty.Type.STRING).
                    constraint(PropertyConstraintType.required, true).
                    build(),
        };
    }

    @Override
    public String getName() {
        return "Pushover";
    }

    @Override
    public void onStartup(PropertyContainer config) {
        processConfig(config);
    }

    @Override
    public void onShutdown() {

    }

    @Override
    public void onPluginConfigurationUpdate(PropertyContainer config) {
        processConfig(config);
    }

    @Override
    public void onHttpResponse(HttpResponse response, Object context) {
        try {
            if (response.getStatusCode() >= 400 && response.getStatusCode() < 600) {
                logger.error("Error sending Pushover message ({}): {}", response.getStatusCode(), response.getBody());
            } else {
                logger.debug("Pushover message result ({}): {}", response.getStatusCode(), response.getBody());
            }
        } catch (IOException e) {
            logger.error("Error processing HTTP response", e);
        }
    }

    @Override
    public void onHttpRequestFailure(Throwable cause, Object context) {
        logger.error("Failed to send Pushover message", cause);
    }

    public String getUserKey() {
        return userKey;
    }

    public String getApiToken() {
        return apiToken;
    }

    private void processConfig(PropertyContainer config) {
        userKey = config.getStringPropertyValue("userKey");
        apiToken = config.getStringPropertyValue("apiToken");

        if (userKey != null && apiToken != null) {
            setStatus(PluginStatus.running());
            if (!actionProviderPublished) {
                publishActionProvider(new PushoverActionProvider(this));
                actionProviderPublished = true;
            }
        } else {
            setStatus(PluginStatus.notConfigured(""));
        }
    }
}
