package com.company;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main {

    public static void main(String[] args) {
        int currency = 50;

        CyclicBarrier cb = new CyclicBarrier(currency);//通过它可以实现让一组线程等待至某个状态之后再全部同时执行

        //CreatService creatService = new CreatNumImp1();//没加锁的订单编号服务类
        //CreatService creatService = new CreatNumImpWithLock();//加了普通锁的订单编号服务类

        for (int i = 0; i < currency; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CreatService creatService = new CreateNumImpWiehZkLock();//模拟多台服务器的并发处理，加了分布式锁的订单编号服务类
                    System.out.println(Thread.currentThread().getName() + "-----------准备好了-------------");
                    try {
                        cb.await();//模拟并环境

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    creatService.CreatNum();
                }
            }).start();
        }

        /*ZkClient client = new ZkClient("localhost:2181");
        client.setZkSerializer(new MyZkSerializer());
        client.subscribeDataChanges("/mike/a", new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception{
                System.out.println("---------节点被改变了:"+ o +"---------");
            }

            @Override
            public void handleDataDeleted(String s) throws Exception{
                System.out.println("---------节点被删除了---------");
            }
        });

        try {
            Thread.sleep(1000 * 60 * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

    }
}
