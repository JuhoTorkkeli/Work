//Initialize HashNode keys and values here for convenience.
class HashNode<K, V> {
	K key;
	V value;
	
	//Next node reference.
	HashNode<K, V> next;
	
	//Constructor for keys and values.
	public HashNode(K key, V value) {
		this.key = key;
		this.value = value;
	}
}