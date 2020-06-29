package com.legend.springbootzookeeperdemo.registrationcenter;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class DefaultWatcher implements Watcher {
    private static  CountDownLatch cdl;

    public void setLock(CountDownLatch cdl){
        this.cdl = cdl;
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
                cdl.countDown();
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
