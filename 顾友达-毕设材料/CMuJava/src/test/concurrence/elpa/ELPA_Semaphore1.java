/*
 * @author: GuYouda
 * @date: 2018/4/18
 * @time: 10:03
 * @des:
 */

package test.concurrence.elpa;

import java.util.concurrent.Semaphore;

public class ELPA_Semaphore1 {
    // 将信号量设为3
    private Semaphore semaphore = new Semaphore(3);

    public void test1() {
        try {
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName() + " start at " + System.currentTimeMillis());
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + " stop at " + System.currentTimeMillis());
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void test2() {
        try {
            semaphore.acquire(2);
            System.out.println(Thread.currentThread().getName() + " start at " + System.currentTimeMillis());
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + " stop at " + System.currentTimeMillis());
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void test3() {
        try {
            semaphore.tryAcquire();
            System.out.println(Thread.currentThread().getName() + " start at " + System.currentTimeMillis());
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + " stop at " + System.currentTimeMillis());
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void test4() {
        try {
            semaphore.acquireUninterruptibly();
            System.out.println(Thread.currentThread().getName() + " start at " + System.currentTimeMillis());
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + " stop at " + System.currentTimeMillis());
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
