public class MyHashTable<K extends Comparable<K>, V extends Comparable<V>> {
    private DLList<V>[] table;
    private DLList<K> keys;
    
    @SuppressWarnings("unchecked")
    public MyHashTable(){
        table = new DLList[1024];
        keys = new DLList<K>();
    }
    
    public void add(K key, V data){
        if(table[key.hashCode()] == null) {
            table[key.hashCode()] = new DLList<V>();
            keys.add(key);
            table[key.hashCode()].add(data);
        } else {
            table[key.hashCode()].add(data);
        }
    }

    public DLList<V> get(K key){
        return table[key.hashCode()];
    }

    public V get(K key, int i) {
        if(containsKey(key) && table[key.hashCode()].size() > i) {
            return table[key.hashCode()].get(i);
        }
        return null;
    }

    public DLList<K> getKeys() {
        return keys;
    }

    public DLList<V> getTable(int i) {
        if(i<table.length) {
            return table[i];
        } return null;
    }
    
    public String toString(){
        String s = "";
        for (int i = 0; i < keys.size(); i++) {
            K key = keys.get(i);
            s += key.toString() + " (" + table[key.hashCode()].size() + ") " + "\n";
        }
        return s;
    }

    public void remove(K key, V data){
        table[key.hashCode()].remove(data);
        if(table[key.hashCode()].size() == 0){
            table[key.hashCode()] = null;
            keys.remove(key);
        }
    }

    public boolean contains(K key, V data) {
        if(table[key.hashCode()] != null && table[key.hashCode()].contains(data)) {
            return true;
        }
        return false;
    }

    public boolean containsKey(K key) {
        return keys.contains(key);
    }
  
    public void remove(K key){
        table[key.hashCode()] = null;
        keys.remove(key);
    }
}
