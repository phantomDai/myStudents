/*
 * @author: GuYouda
 * @date: 2018/4/22
 * @time: 21:19
 * @des:
 */

package test.concurrence.rxo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RXO1 {
    private Lock lock1 = new ReentrantLock();
    private Lock lock2 = new ReentrantLock();
    private Lock lock3 = new ReentrantLock();

    private void test1() {
        lock1.lock();
        System.out.println("TEST1");
        lock1.unlock();
    }

    private void test2() {
        lock2.lock();
        System.out.println("TEST1");
        lock2.unlock();
    }

}
