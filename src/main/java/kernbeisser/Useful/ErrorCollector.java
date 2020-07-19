package kernbeisser.Useful;

import kernbeisser.Main;

import java.util.HashMap;

public class ErrorCollector {
    private final HashMap<String,Integer> counter = new HashMap<>();


    public void collect(Exception e) {
        Integer before = counter.get(e.getMessage());
        if (before == null) {
            counter.put(e.getMessage(), 1);
        } else {
            counter.replace(e.getMessage(), before + 1);
        }
    }

    public void log() {
        counter.forEach((a, b) -> Main.logger.error("Error: '" + a + "' happened " + b + " times"));
    }

    public int count() {
        return counter.values().stream().mapToInt(Integer::intValue).sum();
    }
}
