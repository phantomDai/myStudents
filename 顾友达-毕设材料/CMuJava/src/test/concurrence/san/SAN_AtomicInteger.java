/*
 * @author: GuYouda
 * @date: 2018/4/18
 * @time: 21:52
 * @des:
 */

package test.concurrence.san;

import java.util.concurrent.atomic.AtomicInteger;

public class SAN_AtomicInteger {
    private AtomicInteger count = new AtomicInteger();

    public void increment() {
        int temp = count.getAndSet(100);
        count.incrementAndGet();
    }

    //使用AtomicInteger之后，不需要加锁，也可以实现线程安全。
    public int test() {
        int tt = 100;
        tt = count.getAndSet(99);
        return count.get();
    }
}
