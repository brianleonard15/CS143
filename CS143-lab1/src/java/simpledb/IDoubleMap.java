package simpledb;

import java.util.Set;

/**
 * The DoubleMap is for when you have two keys for a Hashmap so you can
 * quickly access the value from either key
 */
public interface IDoubleMap<K1, K2, V> {

    void put(K1 key1, K2 key2, V value);

    V getKey1(K1 key1);

    V getKey2(K2 key2);

    Set<K1> getKeySet1();

    Set<K2> getKeySet2();

    void clear();
}

