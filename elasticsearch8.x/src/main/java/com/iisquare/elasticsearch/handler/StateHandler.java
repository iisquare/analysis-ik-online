package com.iisquare.elasticsearch.handler;

import com.iisquare.elasticsearch.plugin.HandlerBase;
import com.iisquare.elasticsearch.wltea.cfg.Configuration;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class StateHandler extends HandlerBase {

    @Override
    public List<Route> routes() {
        return Arrays.asList(
                new Route(RestRequest.Method.GET, uri("state"))
        );
    }

    @Override
    public void handleRequest(RestRequest request, RestChannel channel, NodeClient client) throws Exception {
        Configuration config = Configuration.getInstance();
        message(channel, 0, "success", new LinkedHashMap(){{
            put("url", config.getUrl());
            put("initSerials", Arrays.asList(config.getInitSerials()));
        }});
    }
}
