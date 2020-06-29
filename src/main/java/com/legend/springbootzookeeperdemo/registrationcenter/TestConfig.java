package com.legend.springbootzookeeperdemo.registrationcenter;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfig {
    private ZooKeeper zk;

    @Before
    public void conn(){
        zk = ZKUtils.getZK();
    }

    @After
    public void close(){
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConf(){
        WatcherCallback watcherCallback = new WatcherCallback();
        watcherCallback.setZk(zk);
        MyConf myConf = new MyConf();
        watcherCallback.setMyConf(myConf);
        watcherCallback.await();
        //1.节点不存在
        //2.几点存在的时候
        while(true){
            System.out.println(myConf.getConf());
            System.out.println("AAAAAAAAAAAAAAA");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
