package com.company;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ZkImproveLock implements Lock {
    private String LockPath;
    private ZkClient client;
    private String currentPath;
    private String beforePath;

    public ZkImproveLock(String lockpath) {
        super();
        LockPath = lockpath;
        client = new ZkClient("localhost:2181");
        client.setZkSerializer(new MyZkSerializer());
        if (!this.client.exists(LockPath)) {
            try {
                this.client.createPersistent(LockPath);
            } catch (ZkNodeExistsException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void lock() {
        if (!tryLock()) {
            waitForLock();
            lock();
        }
    }

    private void waitForLock() {
        CountDownLatch cdl = new CountDownLatch(1);
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) {

            }

            @Override
            public void handleDataDeleted(String dataPath) {
                System.out.println("----------监听节点被删除---------");
                cdl.countDown();
            }
        };
        client.subscribeDataChanges(this.beforePath, listener);
        if (this.client.exists(this.beforePath)) {
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void lockInterruptibly() {

    }

    @Override
    public boolean tryLock() {
        if (this.currentPath == null) {
            currentPath = this.client.createEphemeralSequential(LockPath + "/", "aaaa");
        }
        List<String> children = this.client.getChildren(LockPath);
        Collections.sort(children);
        if (currentPath.equals(LockPath + "/" + children.get(0))) {
            return true;
        } else {
            int curIndex = children.indexOf(currentPath.substring(LockPath.length() + 1));
            beforePath = LockPath + "/" + children.get(curIndex - 1);
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        return false;
    }

    @Override
    public void unlock() {
        this.client.delete(this.currentPath);

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
