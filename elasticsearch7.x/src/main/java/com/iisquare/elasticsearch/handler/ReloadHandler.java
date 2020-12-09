package com.iisquare.elasticsearch.handler;

import com.iisquare.elasticsearch.plugin.HandlerBase;
import com.iisquare.elasticsearch.wltea.dic.Dictionary;
import com.iisquare.elasticsearch.wltea.util.DPUtil;
import com.iisquare.elasticsearch.wltea.util.HttpUtil;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;

import java.util.*;

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
        // 获取集群节点信息
        String[] nodeIds = new String[]{"_all"};
        final NodesInfoRequest nodesInfoRequest = new NodesInfoRequest(nodeIds);
        NodesInfoResponse response = client.admin().cluster().nodesInfo(nodesInfoRequest).get();
        // 解析集群节点信息
        List<NodeInfo> nodeList = response.getNodes();
        List<String> list = new ArrayList<>();
        for (NodeInfo nodeInfo : nodeList) {
            TransportAddress publishAddress = nodeInfo.remoteAddress();
            if (null == publishAddress) continue;
            list.add(publishAddress.getAddress() + ":" + publishAddress.getPort());
        }
        if (list.isEmpty()) {
            message(channel, 1500, "未读取到任何存活节点", null);
            return;
        }
        // 执行集群调度，重载全部节点词典
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        boolean status = true;
        for (String nodeName : list) {
            String url = "http://" + nodeName + uri("reload");
            param.put("forceNode", true);
            Object result = DPUtil.parseJSON(HttpUtil.requestPost(url, DPUtil.buildJSON(param)));
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
}
