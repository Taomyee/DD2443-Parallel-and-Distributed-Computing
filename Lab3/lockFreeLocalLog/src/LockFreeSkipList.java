import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockFreeSkipList<T extends Comparable<T>> implements LockFreeSet<T> {
    /* Number of levels */
    private static final int MAX_LEVEL = 16;

    private final Node<T> head = new Node<T>();
    private final Node<T> tail = new Node<T>();

    private final List<Log.Entry>[] logs = new ArrayList[100];

    private final Lock globalLock = new ReentrantLock();

    public LockFreeSkipList() {
        for (int i = 0; i < head.next.length; i++) {
            head.next[i] = new AtomicMarkableReference<Node<T>>(tail, false);
        }
        for (int i = 0; i < logs.length; i++) {
            logs[i] = new ArrayList<>();
        }
    }

    private static final class Node<T> {
        private final T value;
        private final AtomicMarkableReference<Node<T>>[] next;
        private final int topLevel;

        @SuppressWarnings("unchecked")
        public Node() {
            value = null;
            next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[MAX_LEVEL + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node<T>>(null, false);
            }
            topLevel = MAX_LEVEL;
        }

        @SuppressWarnings("unchecked")
        public Node(T x, int height) {
            value = x;
            next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[height + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node<T>>(null, false);
            }
            topLevel = height;
        }
    }

    /* Returns a level between 0 to MAX_LEVEL,
     * P[randomLevel() = x] = 1/2^(x+1), for x < MAX_LEVEL.
     */
    private static int randomLevel() {
        int r = ThreadLocalRandom.current().nextInt();
        int level = 0;
        r &= (1 << MAX_LEVEL) - 1;
        while ((r & 1) != 0) {
            r >>>= 1;
            level++;
        }
        return level;
    }

    public boolean add(T x) {
        return add(-1, x);
    }

    @SuppressWarnings("unchecked")
    public boolean add(int threadId, T x) {
        int topLevel = randomLevel();
        int bottomLevel = 0;
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
        while (true) {
            boolean found = find(x, preds, succs);
            addLogEntry(threadId ,"add", new Object[]{x}, !found);
            if (found) {
                return false;
            } else {
                Node<T> newNode = new Node(x, topLevel);
                // update newNode.next
                for (int level = bottomLevel; level <= topLevel; level++) {
                    Node<T> succ = succs[level];
                    newNode.next[level].set(succ, false);
                }
                Node<T> pred = preds[bottomLevel];
                Node<T> succ = succs[bottomLevel];
                // update preds.next
                if (!pred.next[bottomLevel].compareAndSet(succ, newNode, false, false)) {
                    // addLogEntry("add", new Object[]{x}, true);
                    continue;
                }
                for (int level = bottomLevel + 1; level <= topLevel; level++) {
                    while (true) {
                        pred = preds[level];
                        succ = succs[level];
                        // check if pred.next[level] is succs[level], if so, update pred.next[level] to newNode
                        if (pred.next[level].compareAndSet(succ, newNode, false, false))
                            break;
                        // maybe other threads have already updated pred.next[level]
                        // so we need to find the new pred and succ
                        find(x, preds, succs);
                    }
                }
                return true;
            }
        }
    }

    public boolean remove(T x) {
        return remove(-1, x);
    }

    @SuppressWarnings("unchecked")
    public boolean remove(int threadId, T x) {
        int bottomLevel = 0;
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T> succ;
        while (true) {
            boolean found = find(x, preds, succs);
            if (!found) {
                addLogEntry(threadId ,"remove", new Object[]{x}, false);
                return false;
            } else {
                Node<T> nodeToRemove = succs[bottomLevel];
                for (int level = nodeToRemove.topLevel; level >= bottomLevel + 1; level--) {
                    boolean[] marked = {false};
                    succ = nodeToRemove.next[level].get(marked);
                    while (!marked[0]) {
                        nodeToRemove.next[level].compareAndSet(succ, succ, false, true);
                        succ = nodeToRemove.next[level].get(marked);
                    }
                }
                boolean[] marked = {false};
                succ = nodeToRemove.next[bottomLevel].get(marked);
                while (true) {
                    boolean iMarkedIt = nodeToRemove.next[bottomLevel].compareAndSet(succ, succ, false, true);
                    addLogEntry(threadId ,"remove", new Object[]{x}, iMarkedIt);
                    succ = succs[bottomLevel].next[bottomLevel].get(marked);
                    if (iMarkedIt) {
                        find(x, preds, succs);
                        return true;
                    } else if (marked[0]) {
                        return false;
                    }
                }
            }
        }
    }

    public boolean contains(T x) {
        return contains(-1, x);
    }

    public boolean contains(int threadId, T x) {
        int bottomLevel = 0;
        int key = x.hashCode();
        boolean[] marked = {false};
        Node<T> pred = head;
        Node<T> curr = null;
        Node<T> succ = null;
        for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
            curr = pred.next[level].getReference();
            while (true) {
                succ = curr.next[level].get(marked);
                while (marked[0]) {
                    curr = succ;
                    succ = curr.next[level].get(marked);
                }
                if (curr.value != null && x.compareTo(curr.value) < 0) {
                    pred = curr;
                    curr = succ;
                } else {
                    break;
                }
            }
        }
        boolean result = curr.value != null && x.compareTo(curr.value) == 0;
        addLogEntry(threadId ,"contains", new Object[]{x}, result);
        return result;
    }

    private boolean find(T x, Node<T>[] preds, Node<T>[] succs) {
        int bottomLevel = 0;
        boolean[] marked = {false}; // whether curr is marked for deletion
        boolean snip; // whether delete curr physically
        Node<T> pred = null;
        Node<T> curr = null;
        Node<T> succ = null;
        retry:
        while (true) {
            pred = head;
            for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
                curr = pred.next[level].getReference();
                while (true) {
                    succ = curr.next[level].get(marked);
                    while (marked[0]) {
                        snip = pred.next[level].compareAndSet(curr, succ, false, false);
                        if (!snip) continue retry;
                        curr = succ;
                        succ = curr.next[level].get(marked);
                    }
                    if (curr.value != null && x.compareTo(curr.value) < 0) {
                        pred = curr;
                        curr = succ;
                    } else {
                        break;
                    }
                }

                preds[level] = pred;
                succs[level] = curr;
            }
            return curr.value != null && x.compareTo(curr.value) == 0;
        }
    }

    public ArrayList<Log.Entry[]> getLog() {
        // Convert the List of log entries to an array
        // System.out.println("Log size: " + logEntries.size());
        ArrayList<Log.Entry[]> logsResult = new ArrayList<>();
        for (int i = 0; i < logs.length; i++) {
            Log.Entry[] logEntries = logs[i].toArray(new Log.Entry[0]);
            logsResult.add(logEntries);
        }
        return logsResult;
    }

    // Helper method to add log entries
    public void addLogEntry(int threadId, String method, Object[] args, Object returnValue) {
        long timestamp = System.nanoTime();
        Log.Entry entry = new Log.Entry(method, args, returnValue, timestamp);
        logs[threadId].add(entry);
    }

    public HashSet<Object> traverse() {
        HashSet<Object> dataOpr = new HashSet<>();
        Node<T> currentNode = head.next[0].getReference(); // Start from the first node

        while (currentNode != null && currentNode != tail) {
            // Access the integer stored in the current node
            T value = currentNode.value;
            dataOpr.add(value);
            // Move to the next node
            currentNode = currentNode.next[0].getReference();
        }
        return dataOpr;
    }
}

