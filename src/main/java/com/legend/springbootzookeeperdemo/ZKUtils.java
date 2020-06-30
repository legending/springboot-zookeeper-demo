package com.legend.springbootzookeeperdemo;

import com.legend.springbootzookeeperdemo.configurationcenter.DefaultWatcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKUtils {
    private static ZooKeeper zk;
    //private static String addr = "192.168.6.31:2181,192.168.6.32:2181,192.168.6.33:2181";
    private static String addr = "172.18.4.101";
    private static DefaultWatcher defaultWatcher = new DefaultWatcher();
    private static CountDownLatch latch = new CountDownLatch(1);

    public static ZooKeeper getZK(String rootPath){
        defaultWatcher.setLock(latch);
        addr += rootPath;
        try {
            zk = new ZooKeeper(addr, 3000, defaultWatcher);
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zk;
    }

}