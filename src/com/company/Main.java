package com.company;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main {

    public static void main(String[] args) {
        int currency = 50;

        CyclicBarrier cb = new CyclicBarrier(currency);

        //CreatService creatService = new CreatNumImp1();
        //CreatService creatService = new CreatNumImpWithLock();

        for (int i = 0; i < currency; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CreatService creatService = new CreateNumImpWiehZkLock();//模拟多台服务器的并发处理
                    System.out.println(Thread.currentThread().getName() + "-----------准备好了-------------");
                    try {
                        cb.await();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    creatService.CreatNum();
                }
            }).start();
        }

        /*ZkClient client = new ZkClient("0.0.0.0/0.0.0.0:2181");
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
