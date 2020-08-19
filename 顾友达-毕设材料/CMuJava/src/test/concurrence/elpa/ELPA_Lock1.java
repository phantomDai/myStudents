/*
 * @author: GuYouda
 * @date: 2018/4/18
 * @time: 10:14
 * @des:
 */

package test.concurrence.elpa;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ELPA_Lock1 {
    private ArrayList<Integer> arrayList = new ArrayList<Integer>();

    public static void main(String[] args) {
        final ELPA_Lock1 test = new ELPA_Lock1();

        new Thread() {
            public void run() {
                test.insert1(Thread.currentThread());
            }
        }.start();

        new Thread() {
            public void run() {
                test.insert2(Thread.currentThread());
            }
        }.start();

        new Thread() {
            public void run() {
                try {
                    test.insert3(Thread.currentThread());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void insert1(Thread thread) {
        Lock lock = new ReentrantLock();    //注意这个地方
        lock.lock();
        try {
            System.out.println(thread.getName() + "得到了锁");
            for (int i = 0; i < 5; i++) {
                arrayList.add(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(thread.getName() + "释放了锁");
            lock.unlock();
        }
    }

    private void insert2(Thread thread) {
        Lock lock = new ReentrantLock();    //注意这个地方
        lock.tryLock();
        try {
            System.out.println(thread.getName() + "得到了锁");
            for (int i = 0; i < 5; i++) {
                arrayList.add(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(thread.getName() + "释放了锁");
            lock.unlock();
        }
    }

    private void insert3(Thread thread) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();    //注意这个地方
        lock.lockInterruptibly();
        try {
            System.out.println(thread.getName() + "得到了锁");
            for (int i = 0; i < 5; i++) {
                arrayList.add(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(thread.getName() + "释放了锁");
            lock.unlock();
        }
    }
}
