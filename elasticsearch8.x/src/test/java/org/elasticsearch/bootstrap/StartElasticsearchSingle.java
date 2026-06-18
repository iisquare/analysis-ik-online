package org.elasticsearch.bootstrap;

import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;

/**
 * 若程序执行受限，请参照config/java.policy配置${JRE_HOME}/lib/security目录下的同名文件。
 *
 * @author Ouyang <iisquare@163.com>
 */
public class StartElasticsearchSingle {

    public static void main(String[] args) throws Exception {
        Policy.setPolicy(new Policy() {
            @Override
            public PermissionCollection getPermissions(CodeSource codesource) {
                Permissions p = new Permissions();
                p.add(new AllPermission());
                return p;
            }
            @Override
            public void refresh() {}
        });
        System.setProperty("es.path.home", "server/single");
        System.setProperty("es.path.conf", "server/single/config");
        Elasticsearch.main(args);
    }

}
