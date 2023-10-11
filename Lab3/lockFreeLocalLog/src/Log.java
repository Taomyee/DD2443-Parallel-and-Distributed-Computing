import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Log {
    private final List<Entry> entries;
    private Log() {
        // Do not implement ???
        this.entries = new ArrayList<>();
    }

    public static boolean validate(LockFreeSet<Integer> lockFreeSet, Entry[] log) {
        HashSet<Object> dataLog = new HashSet<>();
        for (Entry entry: log){
            String method = entry.method;
            Object[] args = entry.args;
            Object returnValue = entry.returnValue;
            //System.out.println("valid "+entry.toString());
            //System.out.println(data.toString());
            if (method.equals("add")){
                if ((boolean) returnValue){
                    dataLog.add(args[0]);
                }
            }
            else if (method.equals("remove")){
                if ((boolean) returnValue){
                    dataLog.remove(args[0]);
                }
            }
        }
        HashSet<Object> dataOpr = lockFreeSet.traverse();
        // compare dataLog and dataOpr
        HashSet<Object> difference1 = new HashSet<>(dataOpr);
        difference1.removeAll(dataLog);
        HashSet<Object> difference2 = new HashSet<>(dataLog);
        difference2.removeAll(dataOpr);
        System.out.println("Discrepancy: " + (difference1.size() + difference2.size()));
        return dataLog.equals(dataOpr);
    }


    public static boolean validate(LockFreeSet<Integer>[] lockFreeSets, Log.Entry[] log) {
        HashSet<Object> dataLog = new HashSet<>();
        for (Entry entry: log){
            String method = entry.method;
            Object[] args = entry.args;
            Object returnValue = entry.returnValue;
            //System.out.println("valid "+entry.toString());
            //System.out.println(data.toString());
            if (method.equals("add")){
                if ((boolean) returnValue){
                    dataLog.add(args[0]);
                }
            }
            else if (method.equals("remove")){
                if ((boolean) returnValue){
                    dataLog.remove(args[0]);
                }
            }
        }
        System.out.println("dataLog size: " + dataLog.size());
        HashSet<Object> dataOprs = new HashSet<>();
        for (LockFreeSet<Integer> set : lockFreeSets) {
            HashSet<Object> dataOpr = set.traverse();
            dataOprs.addAll(dataOpr);
        }
        System.out.println("dataOprs size: " + dataOprs.size());
        // compare dataLog and dataOpr
        HashSet<Object> difference1 = new HashSet<>(dataOprs);
        difference1.removeAll(dataLog);
        HashSet<Object> difference2 = new HashSet<>(dataLog);
        difference2.removeAll(dataOprs);
        System.out.println("Discrepancy: " + (difference1.size() + difference2.size()));
        return dataLog.equals(dataOprs);
    }


    public static Entry[] integrate(ArrayList<Entry[]> logs) {
        // TODO integrate the logs according to timestamps
        //System.out.println("integrate");
        ArrayList<Entry> log = new ArrayList<>();
        int numThreads = logs.size();
        int[] index = new int[numThreads];
        int[] size = new int[numThreads];
        for (int i = 0; i < numThreads; i++) {
            size[i] = logs.get(i).length;
        }
        int totalSize = Arrays.stream(size).sum();
        //System.out.println("totalSize: " + totalSize);
        for (int i = 0; i < totalSize; i++) {
            int minIndex = 0;
            long minTimestamp = Long.MAX_VALUE;
            for (int j = 0; j < numThreads; j++) {
                if (index[j] < size[j]) {
                    if (logs.get(j)[index[j]].timestamp < minTimestamp) {
                        minIndex = j;
                        minTimestamp = logs.get(j)[index[j]].timestamp;
                    }
                }
            }
            log.add(logs.get(minIndex)[index[minIndex]]);
            index[minIndex]++;
        }
        return log.toArray(new Entry[0]);
    }

    // Log entry for linearization point.
    public static class Entry {
        private final String method;
        private final Object[] args;
        private final Object returnValue;
        private final long timestamp;

        public Entry(String method, Object[] args, Object returnValue, long timestamp) {
            // TODO add method, arguments, return value and timestamp.
            this.method = method;
            this.args = args;
            this.returnValue = returnValue;
            this.timestamp = timestamp;
        }
        public String toString(){
            return String.format("%s(%s) = %s @ %d", method, Arrays.toString(args), returnValue, timestamp);
        }
    }
}
