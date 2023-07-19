package dev.tablesalt.dungeon.collection;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * Used for selecting an item at random with
 * respect to its weight (percentage)
 * @param <E>
 */
public class RandomCollection<E> {

    private final NavigableMap<Double,E> map = new TreeMap<>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;

        total += weight;
        map.put(total,result);
        return this;
    }

    public E next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }


}
