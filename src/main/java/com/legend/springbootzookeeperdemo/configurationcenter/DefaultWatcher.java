package com.legend.springbootzookeeperdemo.configurationcenter;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class DefaultWatcher implements Watcher {
    private static  CountDownLatch latch;

    public void setLock(CountDownLatch cdl){
        this.latch = cdl;
    }

    @Override
    public void process(WatchedEvent event) {
        switch(event.getState()){
            case Unknown:
                break;
            case Disconnected:
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                latch.countDown();
                System.out.println("zookeeper client connected");
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
}
