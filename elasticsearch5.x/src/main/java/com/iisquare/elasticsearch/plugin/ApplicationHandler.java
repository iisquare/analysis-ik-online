package com.iisquare.elasticsearch.plugin;

import java.io.IOException;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestRequest;

public class ApplicationHandler extends BaseRestHandler {

	protected ApplicationHandler(Settings settings) {
		super(settings);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected RestChannelConsumer prepareRequest(RestRequest request,
			NodeClient client) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
