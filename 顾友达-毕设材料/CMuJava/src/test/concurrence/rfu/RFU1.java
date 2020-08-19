/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 13:31
 * @des:
 */

package test.concurrence.rfu;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RFU1 {

    private ArrayList<Integer> arrayList = new ArrayList<Integer>();

    public static void main(String[] args) {
        final RFU1 test = new RFU1();

        new Thread("A") {
            public void run() {
                test.insert1(Thread.currentThread());
            }
        }.start();

        new Thread("B") {
            public void run() {
                test.insert2(Thread.currentThread());
            }
        }.start();
    }

    public void insert1(Thread thread) {
        Lock lock = new ReentrantLock();  // 注意这个地方:lock被声明为局部变量
        lock.lock();
        try {
            System.out.println("线程" + thread.getName() + "得到了锁...");
            for (int i = 0; i < 5; i++) {
                arrayList.add(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("线程" + thread.getName() + "释放了锁...");
            lock.unlock();
        }
    }

    public void insert2(Thread thread) {
        Lock lock = new ReentrantLock();  // 注意这个地方:lock被声明为局部变量
        lock.lock();
        try {
            System.out.println("线程" + thread.getName() + "得到了锁...");
            for (int i = 0; i < 5; i++) {
                arrayList.add(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("线程" + thread.getName() + "释放了锁...");
        lock.unlock();

    }

    public void insert3(Thread thread) {
        Lock lock = new ReentrantLock();  // 注意这个地方:lock被声明为局部变量
        lock.lock();
        try {
            System.out.println("线程" + thread.getName() + "得到了锁...");
            for (int i = 0; i < 5; i++) {
                arrayList.add(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("线程" + thread.getName() + "释放了锁...");
        }
        lock.unlock();
    }
}
