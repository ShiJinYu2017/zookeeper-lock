package com.company;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CreatNumImpWithLock implements CreatService {
    //private NumCreat nc = new NumCreat();
    private static NumCreat nc = new NumCreat();//模拟多个订单服务类共用一个订单编号生成器

    private Lock lock = new ReentrantLock();//并发包中的可重入

    @Override
    public void CreatNum() {
        String Num;
        try {
            lock.lock();
            Num = nc.getNum();
        }finally {
            lock.unlock();
        }
        System.out.println(Thread.currentThread().getName() + "==============>" + Num);

    }
}
