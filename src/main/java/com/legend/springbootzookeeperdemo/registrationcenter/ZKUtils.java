package com.legend.springbootzookeeperdemo.registrationcenter;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKUtils {
    private static ZooKeeper zk;
    private static String addr = "192.168.6.31:2181,192.168.6.32:2181,192.168.6.33:2181/RegistrationCenterTest";
    private static DefaultWatcher defaultWatcher = new DefaultWatcher();
    private static CountDownLatch lock = new CountDownLatch(1);

    public static void main(String [] args){
        getZK();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static ZooKeeper getZK(){
        defaultWatcher.setLock(lock);
        try {
            zk = new ZooKeeper(addr, 3000, defaultWatcher);
            try {
                lock.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zk;
    }

}