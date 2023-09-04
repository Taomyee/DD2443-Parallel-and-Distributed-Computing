public class ex1 extends Thread {

    public void run() {
        System.out.println("Thread ID " + Thread.currentThread().getId() + ": Hello World! " );
    }

    public static void main(String args[]) {
        ex1[] thread = new ex1[5];
        for (int i = 0; i < 5; i++) {
            thread[i] = new ex1();
            thread[i].start();
        }

        for (int i = 0; i < 5; i++) {
            try {
                thread[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Goodbye World!");
    }
}