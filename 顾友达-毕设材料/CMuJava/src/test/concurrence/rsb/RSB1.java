/*
 * @author: GuYouda
 * @date: 2018/4/19
 * @time: 10:43
 * @des:
 */

package test.concurrence.rsb;

public class RSB1 implements Runnable {
    private static int count;

    public RSB1() {
        count = 0;
    }

    public  void run() {
        synchronized(this) {
            for (int i = 0; i < 5; i++) {
                try {
                    System.out.println(Thread.currentThread().getName() + ":" + (count++));
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getCount() {
        return count;
    }
}
