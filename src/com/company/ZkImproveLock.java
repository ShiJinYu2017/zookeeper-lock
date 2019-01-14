package com.company;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ZkImproveLock implements Lock {
    private String LockPath;//父节点
    private ZkClient client;
    private String currentPath;//当前子节点
    private String beforePath;//前一个节点

    public ZkImproveLock(String lockpath) {
        super();
        LockPath = lockpath;
        client = new ZkClient("localhost:2181");
        client.setZkSerializer(new MyZkSerializer());
        if (!this.client.exists(LockPath)) {
            //如果不存在父节点，则create一个父节点
            try {
                this.client.createPersistent(LockPath);
            } catch (ZkNodeExistsException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean tryLock() {
        //在父节点下创建临时顺序节点，由于TryLock会被lock多次调用，为了避免多次生成子节点，所以需要判断current==null
        if (this.currentPath == null) {
            currentPath = this.client.createEphemeralSequential(LockPath + "/", "aaaa");
        }
        List<String> children = this.client.getChildren(LockPath);
        Collections.sort(children);
        //如果当前的节点是最小的节点，那么返回真，否则获取前一个节点进行监听
        if (currentPath.equals(LockPath + "/" + children.get(0))) {
            return true;
        } else {
            int curIndex = children.indexOf(currentPath.substring(LockPath.length() + 1));
            beforePath = LockPath + "/" + children.get(curIndex - 1);
        }
        return false;
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
        client.subscribeDataChanges(this.beforePath, listener);//注册watcher,监听前一个节点。
        //如果前一个节点存在，则线程阻塞
        if (this.client.exists(this.beforePath)) {
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        client.unsubscribeDataChanges(this.beforePath, listener);//取消注册。
    }

    @Override
    public void unlock() {
        this.client.delete(this.currentPath);

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
