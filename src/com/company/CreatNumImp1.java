package com.company;

public class CreatNumImp1 implements CreatService {
    private NumCreat nc = new NumCreat();

    @Override
    public void CreatNum() {
        String Num = nc.getNum();
        System.out.println(Thread.currentThread().getName() + "============>" + Num);
    }
}
