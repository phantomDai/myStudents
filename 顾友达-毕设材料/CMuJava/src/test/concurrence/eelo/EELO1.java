/*
 * @author: GuYouda
 * @date: 2018/4/27
 * @time: 10:45
 * @des:
 */

package test.concurrence.eelo;

import java.util.concurrent.locks.ReentrantLock;

public class EELO1 {
    private ReentrantLock lock = new ReentrantLock();
    private ReentrantLock lock1 = new ReentrantLock();

    public void method() {
        try {
            lock.lock();
            for (int i = 1; i <= 3; i++) {
                Thread.sleep(1000);
                System.out.println("ThreadName=" + Thread.currentThread().getName() + "  " + i);
            }
            lock1.lock();
            for (int i = 1; i <= 3; i++) {
                Thread.sleep(1000);
                System.out.println("ThreadName=" + Thread.currentThread().getName() + "  " + i);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            lock1.unlock();
        }
    }
}
