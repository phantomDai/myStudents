/*
 * @author: GuYouda
 * @date: 2018/4/26
 * @time: 9:24
 * @des:
 */

package test.concurrence.mxt;
/**
 * @author QingHe
 * Creation on 2005-12-19
 */

public class MXT_join implements Runnable {
    public static int a = 0;

    public void run() {
        for (int k = 0; k < 5; k++) {
            a = a + 1;
        }
    }

    public static void main(String[] args) throws Exception {
        MXT_join r = new MXT_join();
        r.test1();
        r.test2();
        r.test3();

    }

    private void test1() throws InterruptedException {
        Runnable r = new MXT_join();
        Thread t = new Thread(r);
        t.start();
        t.join();
        System.out.println(a);
    }

    private void test2() throws InterruptedException {
        Runnable r = new MXT_join();
        Thread t = new Thread(r);
        t.start();
        t.join(1000);
        System.out.println(a);
    }

    private void test3() throws InterruptedException {
        Runnable r = new MXT_join();
        Thread t = new Thread(r);
        t.start();
        t.join(1000,500);
        System.out.println(a);
    }
}
