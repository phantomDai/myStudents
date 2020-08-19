/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 8:45
 * @des:
 */

package test.concurrence.san;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class SAN_AtomicIntegerArray1 {

    static AtomicIntegerArray arr = new AtomicIntegerArray(10);
    static CountDownLatch countDownLatch = new CountDownLatch(10);

    static class AddThread implements Runnable {
        public void run() {
            int temp = arr.getAndSet(0, 99);
            for (int k = 0; k < 10000; k++) {
                for (int j = 0; j < 10; j++) {
                    arr.getAndSet(j, k);
                    temp = arr.getAndSet(j, k);
                    arr.getAndIncrement(j); // 每个元素递增
                }
            }
            countDownLatch.countDown();
        }
    }

    public static void test() throws InterruptedException {
        Thread[] ts = new Thread[10];
        for (int k = 0; k < 10; k++) {
            ts[k] = new Thread(new AddThread());
        }
        for (int k = 0; k < 10; k++) {
            ts[k].start();
        }
        countDownLatch.await();
        for (int k = 0; k < 10; k++) {
            System.out.println(arr.get(k));
        }
    }
}
