package com.company;

import java.util.concurrent.locks.Lock;

public class CreateNumImpWiehZkLock implements CreatService {
    private static NumCreat nc = new NumCreat();//模拟多个订单服务类共用一个订单编号生成器
    Lock lock = new ZkLock("/shijinyu777");//v1版本的分布式锁，会发生惊群效应
    //Lock lock = new ZkImproveLock("/shijinyu666");//v2版本的分布式锁，不会发生惊群效应

    @Override
    public void CreatNum() {
        String Num ;
        try {
            lock.lock();
            Num = nc.getNum();
        }finally {
            lock.unlock();
        }
        System.out.println(Thread.currentThread().getName() + "==============>" + Num);
    }
}
