package com.legend.springbootzookeeperdemo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/*
 * 1. zk只有session，没有连接池的概念
 * 2. 在new ZooKeeper时会立马返回一个对象，但真正连接的工作是线程异步去做的，所以要监测zk是否连接成功的需要阻塞连接过程，这里使用CountDownLatch
 * 3. watch回调是一次性的（触发一次就不再发生），znode的watch默认（zk.getData("/lalala", true, stat)）用的是创建zk对象时的回调
 * 4.
 * */

public class TestZookeeper {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

        CountDownLatch cdl = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper("192.168.6.31:2181,192.168.6.32:2181,192.168.6.33:2181",
                300, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
                String path = event.getPath();
                System.out.println("state: " + state.toString() + ", " + "type: " + type.toString() + ", " + "path: " + path);

                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("connected successfully");
                        cdl.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                    case Closed:
                        break;
                }
            }
        });

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(zk.getState());

        String pathName = zk.create("/lalala", "hello".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        Stat stat  = new Stat();
        byte [] znodeData = zk.getData("/lalala", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("Get watched: " + event.toString());
                try {
                    zk.getData("/lalala", true, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);
        System.out.println(znodeData.toString());
        zk.setData("/lalala", "amazing".getBytes(), 0);
        zk.setData("/lalala", "fantastic".getBytes(), 1);

        System.out.println("async callback start");
        zk.getData("/lalala", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                System.out.println("async callback is triggered");
                System.out.println("data" + bytes.toString());
                System.out.println(o.toString());
            }
        }, "abc");
        System.out.println("async callback end");

        Thread.sleep(1000000);//测试stop掉zk集群中的任意一zk实例，发现session不会变，会重新与另外的zk实例建立连接
    }
}
