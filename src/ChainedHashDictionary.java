
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * ChainedHashDictionary is an implementation of IDictionary, which represents an ambiguous
 * data structure filled with (key, value) pairs. This implementation uses the ArrayDictionary
 *  mapping structure as its 'chains', essentially creating a hash table that can contain 
 *  multiple key-value pairs (KVPair objects) under a single index, as opposed to one (with just 
 *  ArrayDictionary). 
 * 
 * Uses separate chaining for collision resolution
**/
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!
    private int buckets;
    private int numEntries;
    
    public ChainedHashDictionary() {
        this.chains = makeArrayOfChains(5);
        this.buckets = chains.length;
        this.numEntries = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        return (IDictionary<K, V>[]) new IDictionary[size];
    }
    
    /**
     * takes in a target key and returns the value associated with that key in this instance of
     * ChainedHashDictionary
     * Throws an exception if the structure does not contain the specified key
     */
    @Override
    public V get(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchElementException();
        }
        int hashKey = hashKeyGenerator(key, buckets);
        return chains[hashKey].get(key);
    }
    
    /**
     * takes in a key and value and inserts it into the structure
     */
    @Override
    public void put(K key, V value) {
        int hashKey = hashKeyGenerator(key, buckets);
        if (chains[hashKey] == null) {
            chains[hashKey] = new ArrayDictionary<K, V>();
        } 
        if (!chains[hashKey].containsKey(key)) {
            numEntries++;
        }
        chains[hashKey].put(key, value);
        if (numEntries/ buckets > 1.00) {
            resize();
        }
    }
    
    /**
     * takes in a desired key and removes the entry associated with that key
     * throws an exception if the structure does not contain the key
     */
    @Override
    public V remove(K key) {
        if (!this.containsKey(key)) {
            throw new NoSuchElementException();
        }
        int hashKey = hashKeyGenerator(key, buckets);
        V returnedValue = chains[hashKey].get(key);
        chains[hashKey].remove(key);
        numEntries--;
        return returnedValue;
    }
    /**
     * takes in a desired key and returns whether or not an entry associated with the key is in 
     * the structure
     */
    @Override
    public boolean containsKey(K key) {
        int hashKey = hashKeyGenerator(key, buckets);
        if (hashKey < chains.length && chains[hashKey] != null && chains[hashKey].containsKey(key)) {
            return true;
        }
        return false;
    }
    /**
     * returns the number of entries inside the hash table
     */
    @Override
    public int size() {
        int runningSum = 0;
        for (int i = 0; i < chains.length; i++) {
            if (chains[i] != null) {
                runningSum += chains[i].size();
            }
        }
        return runningSum;
    }
    
    /**
     * takes in a key and the current number of 'buckets', or columns in the structure and returns
     * the index where an entry with that key is supposed to be placed
     */
    private int hashKeyGenerator(K key, int bucketSize) {
        int hashKey = 0;
        if (key != null) {
            hashKey = key.hashCode();
            if (hashKey < 0) {
                hashKey = hashKey * -1;
            }
            hashKey = hashKey % bucketSize;
        }
        return hashKey;
    }
    /**
     * Internally adjusts the ChainedHashDictionary in order to maintain O(1) search time in table
     * operations
     */
    private void resize() {
        IDictionary<K, V>[]newChains = makeArrayOfChains(buckets * 2 + 1);
        for (int i = 0; i < buckets; i++) {
            if (chains[i] != null) {
                Iterator<KVPair<K, V>> iter = chains[i].iterator();
                for (KVPair<K, V> pair: chains[i]) {
                    int newHashKey = hashKeyGenerator(pair.getKey(), buckets * 2 + 1);
                    if (newChains[newHashKey] == null) {
                        newChains[newHashKey] = new ArrayDictionary<K, V>();
                    }
                    newChains[newHashKey].put(pair.getKey(), pair.getValue());
                }
            }
        }
        buckets = buckets * 2 + 1;
        chains = newChains;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Iterator class
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int currentIndex;
        private Iterator<KVPair<K, V>> currentIterator;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.currentIndex = 0;
            if (chains[currentIndex] != null) {
                this.currentIterator = chains[0].iterator();
            } else {
                while (currentIndex < chains.length-1 && chains[currentIndex] == null) {
                    currentIndex++;
                }
                if (chains[currentIndex] != null) {
                    this.currentIterator = chains[currentIndex].iterator();
                }
            }
        }
        
        /**
         * returns whether there is another value in the hash table
         */
        @Override
        public boolean hasNext() {
            if (currentIterator != null) {
                if (currentIterator.hasNext()) {
                    return true;
                } else if (currentIndex == chains.length) {
                    return false;
                } else {
                    int tempIndex = currentIndex + 1;
                    while (tempIndex < chains.length) {
                        if (chains[tempIndex] != null) {
                            return true;
                        }
                        tempIndex++;
                    }
                }
            }
            return false;
        }
        
        /**
         * Iterates the pointer to the next KVPair object in the table
         */
        @Override
        public KVPair<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else if (currentIterator.hasNext()) {
                return currentIterator.next();
            } else if (currentIndex < chains.length) {
                int nextIndex = currentIndex + 1;
                while (nextIndex < chains.length) {
                    currentIndex++;
                    if (chains[nextIndex] != null) {
                        currentIterator = chains[currentIndex].iterator();
                        return currentIterator.next();
                    }
                    nextIndex++;
                }
            }
            return null;
            
        }
    }
}
