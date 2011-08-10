package com.scalemotion.sort4j.text;

import com.scalemotion.sort4j.MemoryCalculator;

public class StringMemoryCalculator implements MemoryCalculator<String>{
    /**
     * {@inheritDoc}
     */
    public int sizeof(String item) {
        return item.length()*2;
    }
}
