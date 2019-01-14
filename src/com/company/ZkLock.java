package com.company;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ZkLock implements Lock {
    private String lockpath;
    private ZkClient client;

    public ZkLock(String path) {
        super();
        lockpath = path;
        client = new ZkClient("localhost:2181");
        client.setZkSerializer(new MyZkSerializer());
    }


    @Override
    public boolean tryLock() {
        try {
            client.createEphemeral(lockpath);
        } catch (ZkNodeExistsException e) {
            return false;
        }
        return true;
    }

    @Override
    public void lock() {
        if (!tryLock()) {
            waitForLock();
            lock();
        }
    }

    @Override
    public void unlock() {
        client.delete(lockpath);
    }


    private void waitForLock() {
        CountDownLatch cdl = new CountDownLatch(1);
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) {


            }

            @Override
            public void handleDataDeleted(String dataPath) {
                System.out.println("-----------节点被删除了----------------");
                cdl.countDown();
            }
        };
        client.subscribeDataChanges(lockpath, listener);

        if (this.client.exists(lockpath)) {
            try {
                cdl.await();//用wait()将报错Exception in thread "Thread-4" java.lang.IllegalMonitorStateException
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        client.unsubscribeDataChanges(lockpath, listener);

    }

    @Override
    public void lockInterruptibly() {

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
