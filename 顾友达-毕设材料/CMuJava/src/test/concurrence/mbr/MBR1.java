/*
 * @author: GuYouda
 * @date: 2018/4/12
 * @time: 19:52
 * @des:
 */

package test.concurrence.mbr;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MBR1 {
    public void test() {
        int N = 4;
        CyclicBarrier barrier = new CyclicBarrier(N);
        CyclicBarrier barrier1 = new CyclicBarrier(9, new Writer());
        CyclicBarrier barrier2 = new CyclicBarrier(N, new Runnable() {
            @Override
            public void run() {
                System.out.println(".............");
            }
        });
        for (int i = 0; i < N; i++)
            new Writer().start();
    }

    class Writer extends Thread {
        private CyclicBarrier cyclicBarrier;

        @Override
        public void run() {
            System.out.println("线程" + Thread.currentThread().getName() + "正在写入数据...");
            try {
                Thread.sleep(5000);      //以睡眠来模拟写入数据操作
                System.out.println("线程" + Thread.currentThread().getName() + "写入数据完毕，等待其他线程写入完毕");
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("所有线程写入完毕，继续处理其他任务...");
        }
    }
}
