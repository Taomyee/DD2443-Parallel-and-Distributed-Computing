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


    /*public static boolean validate(Log.Entry[] log) {

        HashSet<Object> dataStructure = new HashSet<>();
        int discrepancy = 0;
        for (Log.Entry entry: log){
            String method = entry.method;
            Object[] args = entry.args;
            Object returnValue = entry.returnValue;
            //System.out.println("valid "+entry.toString());
            //System.out.println(dataStructure.toString());
            if (method.equals("add")){
                if (dataStructure.contains(args[0]) == (boolean) returnValue){;
                    discrepancy++;
                }
                if ((boolean) returnValue){
                    dataStructure.add(args[0]);
                }
            }
            else if (method.equals("remove")){
                if (dataStructure.contains(args[0]) != (boolean) returnValue){
                    discrepancy++;
                }
                else{
                    dataStructure.remove(args[0]);
                }
            }
            else if (method.equals("contains")){
                if (dataStructure.contains(args[0]) != (boolean)returnValue){
                    discrepancy++;
                }
            }
            // System.out.println("Discrepancy: " + discrepancy);
        }
        return discrepancy == 0;
    }*/

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
