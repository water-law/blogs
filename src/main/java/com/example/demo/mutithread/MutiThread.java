package com.example.demo.mutithread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author duwei
 * @date 2021/5/12
 */
public class MutiThread {

    static int a = 0;
    static CountDownLatch countDownLatch = new CountDownLatch(10*1000000);

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            MyThread myThread = new MyThread();
            myThread.start();
        }
        countDownLatch.await();
        System.out.println(a);

    }


    static class MyThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 1000000; i++) {
//                a++;
                ++a;
                countDownLatch.countDown();
            }
        }
    }











    public static synchronized void myCompletableFuture() {
        Long st = System.currentTimeMillis();

        String s = CompletableFuture.supplyAsync(() -> {
            try{
                Thread.sleep(7000);
            } catch (InterruptedException e) {
            }
            System.out.println("=========1");
            return "a";
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            System.out.println("=========2");
            return "b";
        }), (a,b)->a+b).join();

        System.out.println(s);
        Long dt = System.currentTimeMillis();
        System.out.println((dt-st)/1000);
    }
}
