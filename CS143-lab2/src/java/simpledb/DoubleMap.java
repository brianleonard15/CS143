package simpledb;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class DoubleMap<K1, K2, V> implements IDoubleMap<K1, K2, V> {

    private final Map<K1, V> map1 = new HashMap<K1, V>();

    private final Map<K2, V> map2 = new HashMap<K2, V>();

    @Override
    public void put(K1 key1, K2 key2, V value) {
        map1.put(key1, value);
        map2.put(key2, value);
    }

    @Override
    public V getKey1(K1 key1) {
        return map1.get(key1);
    }

    @Override
    public V getKey2(K2 key2) {
        return map2.get(key2);
    }

    @Override
    public Set<K1> getKeySet1() { return map1.keySet(); }

    @Override
    public Set<K2> getKeySet2() { return map2.keySet(); }

    @Override
    public void clear() {
        map1.clear();
        map2.clear();
    }

}