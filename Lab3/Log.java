import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Log {
    private final List<Log.Entry> entries;
    private Log() {
        // Do not implement ???
        this.entries = new ArrayList<>();
    }

    // todo
    public static boolean validate(Log.Entry[] log) {
        return false;
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
