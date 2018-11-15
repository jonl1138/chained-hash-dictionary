
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * ArrayDictionary is an implementation of IDictionary, and meant to represent a standard HashMap
 * Stores a set of (Key, Value) pairs (represented by KVPair) 
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    private Pair<K, V>[] pairs;
    private int size;


    public ArrayDictionary() {
        size = 0;
        pairs = makeArrayOfPairs(15);
    }

    /*
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }
    
    /* 
     * Takes in a user-specified key and returns the corresponding Value related to that key from
     * the data structure
     */
    @Override
    public V get(K key) {
        int index = findIndex(key);
        return pairs[index].value;
    }
    /*
     * Takes in a user specified key and value and inserts it into the structure
     */
    @Override
    public void put(K key, V value) {
        Pair<K, V> pair = new Pair<K, V>(key, value);
        
        if (containsKey(key)) {
            int index = findIndex(key);
            pairs[index] = pair;
            
        } else {
            if (size  >= pairs.length) {
                Pair<K, V>[] doublePairs = makeArrayOfPairs(pairs.length * 2); 
                for (int i = 0; i < size; i++) {
                    doublePairs[i] = pairs[i];
                }
                pairs = doublePairs;
            }
            
            pairs[size] = pair;
            size++;
        }
    }
    
    /*
     * With the given key, returns the index.
     * throws exception if key does not exist in array
     */
    private int findIndex(K key) {
        for (int i = 0; i < size; i++) {
            if (pairs[i].key == key || pairs[i].key.equals(key)) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }
    
    /*
     * takes in a desired key, and removes the entry in the ArrayDictionary corresponding to that
     * key, returning the value of the entry
     */
    @Override
    public V remove(K key) {
        int index = findIndex(key);
        
        V value = pairs[index].value;
        
        for (int i = index; i < size - 1; i++) {
            pairs[i] = pairs[i+1];
        }
        
        pairs[size - 1] = null;
        size--;
        
        return value;
    }
    
    /*
     * Takes in a desired key, and returns whether or not the ArrayDictionary contains an entry
     */
    @Override
    public boolean containsKey(K key) {
        for (int i = 0; i < size; i++) {
            if (pairs[i].key != null && pairs[i].key.equals(key) || pairs[i].key == key) {
                return true;
            } 
        }
        return false;
    }
    
    /*
     * returns the number of (key, value) pairs stored in the arraydictionary
     */
    @Override
    public int size() {
        return size;
    }
    
    /*
     * Item class designed to store the hash key of an item, and the value of the item itself
     */
    private static class Pair<K, V> {
        public K key;
        public V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
    
    /**
     * Implements an iterator for an array dictionary
     */
    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        private Pair<K, V>[]pairs;
        private int index;
        private int size;
        
        public ArrayDictionaryIterator(Pair<K, V>[]pairs, int size) {
            this.pairs = pairs;
            this.index = 0;
            this.size = size;
        }
        
        @Override
        public boolean hasNext() {
            return(index < size);
        }

        @Override
        public KVPair<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else {
                K returnedKey = pairs[index].key;
                V returnedValue = pairs[index].value;
                index++;
                return new KVPair<K, V>(returnedKey, returnedValue);
            }
            
        }
        
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<>(this.pairs, this.size);
    }
}
