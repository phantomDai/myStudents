/*
 * @author: GuYouda
 * @date: 2018/4/18
 * @time: 21:52
 * @des:
 */

package test.concurrence.san;

import java.util.concurrent.atomic.AtomicInteger;

public class SAN_AtomicInteger1 {
    private AtomicInteger count = new AtomicInteger();

    public void increment() {
        int temp = count.getAndSet(1);
        count.incrementAndGet();
    }

    //使用AtomicInteger之后，不需要加锁，也可以实现线程安全。
    public int test() {
        int tt = 100;
        tt = count.getAndSet(2);
        return count.get();
    }

    public void test1() {
        int tt = 100;
        for (int i = count.getAndSet(3); i <= 100; i++) {
            System.out.println(i);
        }
        for (int i = 0; i <= count.getAndSet(4); i++) {
            System.out.println(i);
        }

        for (int i = 0; i <= 100; count.getAndSet(5)) {
            System.out.println(i);
        }

    }

    public void test2() {
        int tt = 100;
        while (count.getAndSet(6) > 1) {
            System.out.println("");
        }
    }

    public void test3() {
        int tt = 100;
        for (int i = 0; i <= 10; i++) {
            System.out.println("Count:" + count.getAndSet(7));
            System.out.println(count.getAndSet(8));
            tt(count.getAndSet(9));
        }
    }

    public void tt(int i) {
        System.out.println(i);
    }
}
