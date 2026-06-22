package com.iisquare.elasticsearch.handler;

import com.iisquare.elasticsearch.plugin.HandlerBase;
import com.iisquare.elasticsearch.wltea.dic.Dictionary;
import com.iisquare.elasticsearch.wltea.util.ApiUtil;
import com.iisquare.elasticsearch.wltea.util.DPUtil;
import com.iisquare.elasticsearch.wltea.util.HttpUtil;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.client.internal.node.NodeClient;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.http.HttpInfo;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.RestActionListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 受限于线程池大小，若Handler中存在等待调用，可能会导致请求死锁
 */
public class ReloadHandler extends HandlerBase {

    @Override
    public List<Route> routes() {
        return Arrays.asList(
                new Route(RestRequest.Method.POST, uri("reload"))
        );
    }

    @Override
    public void handleRequest(RestRequest request, RestChannel channel, NodeClient client) throws Exception {
        Map<String, Object> param = param(request);
        String dictSerial = DPUtil.parseString(param.get("dictSerial"));
        boolean forceNode = !DPUtil.empty(param.get("forceNode"));
        String[] dicts = array(param.get("dicts"));
        if (DPUtil.empty(dicts)) {
            message(channel, 1001, "参数错误", null);
            return;
        }
        if (forceNode) {
            LinkedHashMap<String, Boolean> map = Dictionary.getSingleton(dictSerial).reload(dicts);
            if (map.get("status")) {
                message(channel, 0, "载入成功", map);
            } else {
                message(channel, 1500, "载入失败", map);
            }
            return;
        }
        if (param.containsKey("forceNode")) {
            message(channel, 1001, "请勿将forceNode设置为空值", null);
            return;
        }
        // 提取认证头，用于集群节点间转发
        final String authHeader = request.header("Authorization");
        // 根据原始请求的 scheme 决定转发使用的协议，ES 8.x 开启 security 后通常为 https
        // ES 8.19+ HttpRequest 不再提供 scheme() 方法，通过 X-Forwarded-Proto 头获取
        final String scheme = DPUtil.empty(request.header("X-Forwarded-Proto")) ? "http" : request.header("X-Forwarded-Proto");
        // 获取集群节点信息
        final NodesInfoRequest nodesInfoRequest = new NodesInfoRequest();
        nodesInfoRequest.clear().addMetric("http");
        nodesInfoRequest.timeout(TimeValue.timeValueMillis(3000));
        client.admin().cluster().nodesInfo(nodesInfoRequest, new RestActionListener<NodesInfoResponse>(channel) {
            @Override
            public void processResponse(final NodesInfoResponse nodesInfoResponse) {
                // 解析集群节点信息
                List<NodeInfo> nodeList = nodesInfoResponse.getNodes();
                Map<String, String> nodeMap = new LinkedHashMap<>();
                for (NodeInfo nodeInfo : nodeList) {
                    TransportAddress address = nodeInfo.getInfo(HttpInfo.class).getAddress().publishAddress();
                    nodeMap.put(nodeInfo.getNode().getId(), address.getAddress() + ":" + address.getPort());
                }
                // 构建认证请求头
                Map<String, String> headers = new HashMap<>();
                if (!DPUtil.empty(authHeader)) {
                    headers.put("Authorization", authHeader);
                }
                // 执行集群调度，重载全部节点词典
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                boolean status = true;
                for (Map.Entry<String, String> entry : nodeMap.entrySet()) {
                    String nodeId = entry.getKey();
                    String nodeName = entry.getValue();
                    Object result;
                    if (client.getLocalNodeId().equals(nodeId)) { // 当前节点直接执行本地调用，避免远程调用死锁
                        LinkedHashMap<String, Boolean> current = Dictionary.getSingleton(dictSerial).reload(dicts);
                        if (current.get("status")) {
                            result = ApiUtil.result(0, "载入成功", current);
                        } else {
                            result = ApiUtil.result(1500, "载入失败", current);
                        }
                    } else {
                        String url = scheme + "://" + nodeName + uri("reload");
                        param.put("forceNode", true);
                        result = DPUtil.parseJSON(HttpUtil.requestPost(url, DPUtil.buildJSON(param), headers));
                    }
                    if (null == result) {
                        status = false;
                    } else {
                        status &= 0 == DPUtil.parseInt(((Map<?, ?>) result).get("code"));
                    }
                    map.put(nodeName, result);
                }
                if (status) {
                    message(channel, 0, "集群执行载入成功", map);
                } else {
                    message(channel, 1500, "集群执行载入失败", map);
                }
            }
        });
    }
}
