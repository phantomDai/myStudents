package test.concurrence.esp;

public class ESP1 implements Runnable {
    private static int count;
    private final Object object0 = new Object();
    private final Object object1 = new Object();

    public ESP1() {
        count = 0;
    }

    public void run() {
        synchronized (object0) {
            for (int i = 0; i < 5; i++) {
                synchronized (object1) {
                    System.out.println(Thread.currentThread().getName() + ":" + (count++));
                }
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
