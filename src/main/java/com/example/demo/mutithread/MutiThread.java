package com.example.demo.mutithread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author duwei
 * @date 2021/5/12
 */
public class MutiThread {

    public static void main(String[] args) {


//        ThreadPoolExecutor pool = new ThreadPoolExecutor();
//        pool.submit();
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
