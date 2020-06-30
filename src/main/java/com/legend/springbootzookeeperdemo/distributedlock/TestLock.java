package com.legend.springbootzookeeperdemo.distributedlock;

import com.legend.springbootzookeeperdemo.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestLock {
    private ZooKeeper zk;

    @Before
    public void conn(){
        zk = ZKUtils.getZK("/DistributedLockTest");
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
    public void lock(){
        for(int i=0; i<10; i++){
            new Thread(()->{
                LockWatcherCallback watcherCallback = new LockWatcherCallback();
                watcherCallback.setZk(zk);
                String threadName = Thread.currentThread().getName();
                watcherCallback.setThreadName(threadName);
                //每个线程抢锁
                watcherCallback.tryLock();
                //干活
                System.out.println(threadName + " got lock and is working...");
               /* try {//这里如果不sleep一段时间，有可能只有第一线程可以执行任务，因为第一个抢到锁的线程可能非常快完成了任务，但第二个节点还没来的及注册callback第一个节点就被删除了
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                //释放锁
                watcherCallback.unlock();
            }).start();
        }
        while(true) {

        }
    }
}
