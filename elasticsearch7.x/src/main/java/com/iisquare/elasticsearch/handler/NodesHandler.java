package com.iisquare.elasticsearch.handler;

import com.iisquare.elasticsearch.plugin.HandlerBase;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.http.HttpInfo;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.RestActionListener;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NodesHandler extends HandlerBase {

    @Override
    public List<Route> routes() {
        return Arrays.asList(
                new Route(RestRequest.Method.GET, uri("nodes"))
        );
    }

    @Override
    public void handleRequest(RestRequest request, RestChannel channel, NodeClient client) throws Exception {
        final NodesInfoRequest nodesInfoRequest = new NodesInfoRequest();
        nodesInfoRequest.clear().addMetric(NodesInfoRequest.Metric.HTTP.metricName());
        nodesInfoRequest.timeout(TimeValue.timeValueMillis(3000));
        client.admin().cluster().nodesInfo(nodesInfoRequest, new RestActionListener<NodesInfoResponse>(channel) {
            @Override
            public void processResponse(final NodesInfoResponse nodesInfoResponse) {
                List<NodeInfo> nodeList = nodesInfoResponse.getNodes();
                Map<String, String> nodeMap = new LinkedHashMap<>();
                for (NodeInfo nodeInfo : nodeList) {
                    TransportAddress address = nodeInfo.getInfo(HttpInfo.class).getAddress().publishAddress();
                    nodeMap.put(nodeInfo.getNode().getId(), address.getAddress() + ":" + address.getPort());
                }
                message(channel, 0, "操作成功", nodeMap);
            }
        });
    }

}
