package com.huawei.mfg.conf;

import com.huawei.mfg.util.Pair;

import static com.huawei.mfg.util.CommonConstants.*;

/**
 * single or cluster-mode Redis configuration
 */
public class MfgRedisDatasourceConfiguration extends MfgConfiguration {

    public MfgRedisDatasourceConfiguration() {
        super();
    }

    public String getClusterMode() {
        return this.getString(REDIS_CLUSTER_MODE);
    }

    public void setClusterMode(String clusterMode) {
        this.setStringProperty(REDIS_CLUSTER_MODE, clusterMode);
    }

    public void setSingleNodeHost(String host) {
        this.setStringProperty(REDIS_SINGLE_NODE_HOST, host);
    }

    public String getSingleNodeHost() {
        return this.getString(REDIS_SINGLE_NODE_HOST);
    }

    public void setSingleNodePort(int port) {
        this.setIntegerProperty(REDIS_SINGLE_NODE_PORT, port);
    }

    public int getSingleNodePort() {
        return this.getInteger(REDIS_SINGLE_NODE_PORT);
    }

    public void setClusterNodes(int nodes) {
        this.setIntegerProperty(REDIS_CLUSTER_NODES, nodes);
    }

    public int getClusterNodes() {
        return this.getInteger(REDIS_CLUSTER_NODES);
    }

    public void addClusterNode(int node, String host, int port) {
        this.setStringProperty(String.format("%s.%d", REDIS_CLUSTER_HOST_PREFIX, node), host);
        this.setIntegerProperty(String.format("%s.%d", REDIS_CLUSTER_PORT_PREFIX, node), port);
    }

    public Pair<String, Integer> getClusterNode(int node) {
        int port = this.getInteger(String.format("%s.%d", REDIS_CLUSTER_PORT_PREFIX, node));
        String host = this.getString(String.format("%s.%d", REDIS_CLUSTER_HOST_PREFIX, node));
        return Pair.of(host, port);
    }

    @Override
    public String toString() {
        return "MfgRedisDatasourceConfiguration: " + super.toString();
    }
}
