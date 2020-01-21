package proguard;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Profiler {

    private final static ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();

    private final static Map<String, Long> elaspedTime = new LinkedHashMap<>();

    static void timing(final String tag, final Runnable runnable) {
        final long t0 = threadMxBean.getCurrentThreadCpuTime();
        runnable.run();
        final long t1 = threadMxBean.getCurrentThreadCpuTime();
        final long delta = (t1 - t0) / 1000000;
        elaspedTime.put(tag, delta);
        System.err.println("\u001B[32m#### " + tag + ": " + delta + "ms\u001b[0m");
    }

    static void printElapsedTime() {
        final long w1 = elaspedTime.keySet().stream().mapToInt(String::length).max().orElse(0) + 1;
        final DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        final DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        elaspedTime.forEach((tag, elapsed) -> {
            System.out.println(String.format("%1$-" + (w1 - tag.length()) + "s", tag) + " : " + formatter.format(elapsed) + " ms");
        });
    }

}

class Lambda {

    static <E extends Throwable> void unchecked(final Block<? super E> block) {
        try {
            block.invoke();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}

interface Block<E extends Throwable> {
    void invoke() throws E;
}
