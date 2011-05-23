package sort4j.text;

import sort4j.MemoryCalculator;

public class StringMemoryCalculator implements MemoryCalculator<String>{
    public int sizeof(String item) {
        return item.length()*2;
    }
}
