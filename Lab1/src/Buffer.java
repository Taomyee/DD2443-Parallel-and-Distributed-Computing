/**
 * ex4
 */


import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    private final LinkedList<Integer> buffer;
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private boolean closed = false;

    public Buffer(int N) {
        this.capacity = N;
        this.buffer = new LinkedList<>();
    }

    public void add(int i) throws InterruptedException {
        lock.lock();
        try {
            while (buffer.size() == capacity) {
                if (closed) {
                    throw new IllegalStateException("Buffer is full and closed");
                }
                notFull.await();
            }
            buffer.offer(i); // FIFO: add to the end of the queue

            notEmpty.signal(); // Signal consumers that the buffer is not empty
        } finally {
            lock.unlock();
        }
    }

    public int remove() throws InterruptedException {
        lock.lock();
        try {
            while (buffer.isEmpty()) {
                if (closed) {
                    throw new IllegalStateException("Buffer is closed and empty");
                }
                notEmpty.await();
            }
            int value = buffer.poll(); // FIFO: remove from the front of the queue
            notFull.signal(); // Signal producers that the buffer is not full
            return value;
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        lock.lock();
        try {
            if (closed) {
                throw new IllegalStateException("Buffer is already closed");
            }
            closed = true;
            notEmpty.signalAll(); // Signal all waiting consumers
            notFull.signalAll();  // Signal all waiting producers
        } finally {
            lock.unlock();
        }
    }

    public void producerThread() {
        try {
            for (int i = 1; i <= 1000000; i++) {
                add(i);
            }
            close();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void consumerThread() {
        try {
            while (true) {
                int value = remove();
                System.out.println("Consumed: " + value);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IllegalStateException e) {
            // Buffer is closed, terminate
            System.out.println("Consumer thread terminated.");
        }
    }

    public static void main(String[] args) {
        Buffer buffer = new Buffer(10);

        Thread producerThread = new Thread(buffer::producerThread); //runnable method reference
        Thread consumerThread = new Thread(buffer::consumerThread);

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
