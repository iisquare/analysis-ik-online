package com.iisquare.elasticsearch.plugin;

import java.io.IOException;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;

public class ApplicationHandler extends BaseRestHandler {

	@Inject
	public ApplicationHandler(Settings settings) {
		super(settings);
		System.out.println("@ApplicationHandler");
	}

	@Override
	protected RestChannelConsumer prepareRequest(RestRequest request,
			NodeClient client) throws IOException {
		System.out.println("@ApplicationHandler.prepareRequest");
		return new RestChannelConsumer() {

			@Override
			public void accept(RestChannel channel) throws Exception {
				System.out.println("@RestChannelConsumer.accept");
			}
			
		};
	}

}
