
package com.legend.springbootzookeeperdemo.configurationcenter;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WatcherCallback implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {
    private ZooKeeper zk;
    private MyConf myConf;
    private CountDownLatch latch = new CountDownLatch(1);

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public MyConf getMyConf() {
        return myConf;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }

    public void await(){
        zk.exists("/AppConf", this, this, "abc");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processResult(int code, String path, Object ctx, byte[] data, Stat stat) {//获取数据后执行的回调方法
        if(data != null){
            String s = new String(data);
            myConf.setConf(s);
            latch.countDown();
        }
    }

    @Override
    public void processResult(int code, String path, Object o, Stat stat) {//获取状态（exists）后执行的回调方法
        if(stat != null){
            zk.getData("/AppConf", this, this, "abc");
        }
    }

    @Override
    public void process(WatchedEvent event) {//事件的回调方法
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                System.out.println("Node is created");
                zk.getData("/AppConf", this, this, "abc");
                break;
            case NodeDeleted:
                System.out.println("Node is deleted");
                myConf.setConf("");
                latch = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                zk.getData("/AppConf", this, this, "abc");
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            case PersistentWatchRemoved:
                break;
        }
    }
}
