package com.legend.springbootzookeeperdemo.distributedlock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class LockWatcherCallback implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {
    private ZooKeeper zk;
    private String threadName;
    private CountDownLatch latch = new CountDownLatch(1);
    private String pathName;

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public void tryLock(){
        try {
            zk.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "abc");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unlock(){
        try {
            zk.delete(pathName, -1);//delete动作会触发下一个节点
            System.out.println(threadName + " finished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        //第一个获得锁的线程释放了，只会通知持有后面一个锁的线程
        //某个线程挂了也是同样的道理
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "abc");
                break;
            case NodeDataChanged:
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

    @Override
    public void processResult(int code, String path, Object ctx, String name) {//callback after creating node
        if(name != null){
            System.out.println(threadName + " created " + name);
            pathName = name;
            zk.getChildren("/", false, this, "abc");
        }
    }

    @Override
    public void processResult(int code, String path, Object ctx, List<String> children, Stat stat) {//children callback
        //某个线程获取到的节点一定可以看到它前面的节点
        System.out.println(threadName + " can see below nodes:");
        for (String child : children) {
            System.out.println(child);//通过打印可以看出：拿回的children是乱序的
        }
        Collections.sort(children);
        int index = children.indexOf(pathName.substring(1));
        if (index == 0) {//如果是第一个则获取锁
            System.out.println(threadName + " is first and got lock");
            try {
                zk.setData("/", threadName.getBytes(), -1);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        } else {//如果不是第一个则watch自己前面的节点
            zk.exists("/"+children.get(index-1), this, this, "abc");
        }
    }

    @Override
    public void processResult(int code, String path, Object ctx, Stat stat) {

    }
}
