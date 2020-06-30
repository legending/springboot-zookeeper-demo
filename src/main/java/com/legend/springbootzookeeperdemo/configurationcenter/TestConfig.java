package com.legend.springbootzookeeperdemo.configurationcenter;

import com.legend.springbootzookeeperdemo.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
* 如果节/AppConf节点本身不存在，然后新建是可以捕获到的
* 但节点如果存在，删除时可以捕获到，如果再新建就捕获不到了，如果想重新捕获到就只能在while(true)循环里再次判断并调用await
* 由此可见watch是一次性的
* */

public class TestConfig {
    private ZooKeeper zk;

    @Before
    public void conn(){
        zk = ZKUtils.getZK("ConfigurationCenterTest");
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
            if(myConf.getConf().equals("")){
                watcherCallback.await();
            }else{
                System.out.println(myConf.getConf());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
