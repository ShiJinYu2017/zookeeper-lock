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
    //重写生成函数
    public ZkLock(String path) {
        super();
        lockpath = path;
        client = new ZkClient("localhost:2181");
        client.setZkSerializer(new MyZkSerializer());
    }


    @Override
    public boolean tryLock() {
        try {
            client.createEphemeral(lockpath);//创建临时节点，避免死锁
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
        client.delete(lockpath);//删除临时节点
    }


    private void waitForLock() {
        CountDownLatch cdl = new CountDownLatch(1);//利用其类似计数器的功能，实现阻塞
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) {
            }

            @Override
            public void handleDataDeleted(String dataPath) {
                System.out.println("-----------节点被删除了----------------");
                cdl.countDown();//唤醒
            }
        };
        client.subscribeDataChanges(lockpath, listener);//注册Watcher添加对指定节点的监听

        if (this.client.exists(lockpath)) {
            //如果存在该节点，则本线程阻塞
            try {
                cdl.await();//用wait()将报错Exception in thread "Thread-4" java.lang.IllegalMonitorStateException
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        client.unsubscribeDataChanges(lockpath, listener);//取消注册

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
