package test.concurrence.msp;

public class MSP2 implements Runnable {
    private static int count;
    private Object object = new Object();
    private Object object1 = new Object();
    private Object object2 = new Object();
    private Object object3 = new Object();

    public MSP2() {
        count = 0;
    }

    public void run() {
        synchronized (object) {
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
