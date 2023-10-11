import java.util.ArrayList;
import java.util.HashSet;

public interface LockFreeSet<T extends Comparable<T>> {
        // Add an element using thread `threadId`.
        boolean add(T item);
        boolean add(int threadId, T item);
        // Remove an element using thread `threadId`.
        boolean remove(T item);
        boolean remove(int threadId, T item);
        // Check if an element is present using thread `threadId`.
        boolean contains(T item);
        boolean contains(int threadId, T item);
        // Get the log.
        ArrayList<Log.Entry[]> getLog();
        // Traverse the list and print the elements.
        HashSet<Object> traverse();
        void addLogEntry(int threadId, String method, Object[] args, Object returnValue);
}
