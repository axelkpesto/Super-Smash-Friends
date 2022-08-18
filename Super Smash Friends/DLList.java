public class DLList<E extends Comparable<E>> {

	//Global Head and Tail Node
	private Node<E> head;
	private Node<E> tail;

	public DLList() {
		head = null;
		tail = null;
	}

    //Add an object sorted
	public void addSorted(E data) {
		Node<E> current = head;
		Node<E> temp = new Node<E>(data);
	
		if (head == null || head.get().compareTo(data)>0) {
			addFront(data);
		}
		else {
			while (current.next() != null && current.next().get().compareTo(data) < 0) {
				current = current.next();
			}

			temp.setNext(current.next());
			current.setNext(temp);
		}
	}

	//Add an Object to the End
	public void add(E data) {
		Node<E> temp = new Node<E>(data);
		if(head == null) {
			head = temp;
			tail = temp;
		} else {
			Node<E> current = head;
			while(current != null) {
				if(current.next() != null) {
					current = current.next();
				} else {
					current.setNext(temp);
					current.next().setPrev(current);
					tail.setPrev(current);
					break;
				}
			}
		}
	}

	//Remove and return last index
    public E pop() {
        Node<E> temp = tail;
        tail = tail.prev();
        tail.setNext(null);
        return temp.get();
    }

	//Add an Object in an Index
	public void add(int index, E data) {
        if(index>size()) {
            throw new NullPointerException();
        }

		Node<E> temp = new Node<E>(data);
		if (head == null) {
			head = temp;
			tail = temp;
		} else if (index == 0) {
			temp.setNext(head);
			head.setPrev(temp);
			head = temp;
		} else if (index == size()) {
			temp.setPrev(tail);
			tail.setNext(temp);
			tail = temp;
		} else {
			Node<E> current = head;
			for (int i = 1; i < index; i++) {
				current = current.next();
			}

			temp.setNext(current.next());
			current.setNext(temp);
			temp.setPrev(current);
			temp.next().setPrev(temp);
		}
	}

	//Add Object to Index 0
    public void addFront(E data) {
        Node<E> temp = new Node<E>(data);
        temp.setNext(head);
		temp.setPrev(null);
        head = temp;
    }

	//Get Size of List
	public int size() {
		int size = 0;

		Node<E> current = head;

		while(current != null) {
			if(current.next() != null) {
				size++;
				current = current.next();
			} else {
				size++;
				break;
			}
		}
		return size;
	}

	//Count Instances of Object
	public int countInstances(E data) {
		Node<E> current = head;
		int instances = 0;

		if(head.get().equals(data)) {
			instances++;
		}

		while(current != null) {
			if(current.get().equals(data)) {
				instances++;
			}
			current = current.next();
		}
		return instances;
	}

	//Remove Object from a Certain Index
	public void removeIndex(int index) {
        if(index>size()) {
            throw new NullPointerException();
        } 
        
        if(index+1>= 0 && index+1 <= size()) {
			Node<E> current = head; 
			if(index == 0) {
				removeFirst();
			} else if(index == size()-1) {
				removeLast();
			} else {
				for(int i=0; i<index && current.next() != null; i++) {
					current = current.next();
				}
				current.prev().setNext(current.next());
				current.next().setPrev(current.prev());
			}
		}
	}

	public void removeFirst() {
		if(head != null) {
			if(head.next() == null) {
				tail = null;
			} else {
				head.next().setPrev(null);
			}
			head = head.next();
		}
	}

	public void removeLast() {
		if(tail != null) {
			if(head.next() == null) {
				head = null;
			} else {
				tail.prev().setNext(null);
			}
			tail = tail.prev();
		}
	}

	public void shuffle() {
		for(int i=0; i<size(); i++) {
			int rand = (int)(Math.random()*size());
			E data = get(i);
			E tempData = get(rand);
			
			getNode(i).setData(tempData);
			getNode(rand).setData(data);
		}
	}

	//Remove First Iteration of Object
	public void remove(E data) {
		if(data==null) {
			return;
		}
		boolean dataFound = false;
		Node<E> current = head; 
		while(!dataFound) {
			if(current.get().equals(data)) {
				Node<E> previous = current.prev();
				Node<E> next = current.next();
				if(previous != null) {
					previous.setNext(next);
				}
				else {
					head = next;
				}
				if(next !=null) {
					next.setPrev(previous);
				}
				dataFound=true;
			}
			else {
				current= current.next();
			}
		}
	}

	//Return Index of Searched Object
	public int indexOf(E data) {
		
		Node<E> current = head;
		int index = 0;
		
		while(current != null && !current.get().equals(data)) {
			index ++;
			current = current.next();
		}

		if(current != null) {
			return index;
		}
		return -1;
	}

	//Return Object at Specific Index
	public E get(int index) {
		
		Node<E> current = head;
		int currentIndex = 0;

        if(index>size()) {
            throw new NullPointerException();
        }
		
		while(current != null && currentIndex != index) {
			currentIndex++;
			current = current.next();
		}

		if(current != null) {
			return current.get();
		}
		
		return null;
	}

	//Return Object at Specific Index
	public Node<E> getNode(int index) {
	
		Node<E> current = head;
		int currentIndex = 0;

        if(index>size()) {
            throw new NullPointerException();
        }
		
        while(current != null && currentIndex != index) {
			currentIndex++;
			current = current.next();
		}

		if(current != null) {
			return current;
		}
		return null;
	}

	//Return Data of Searched Object
	public E get(E data) {
	
		Node<E> current = head;
		
		while(current != null && !current.get().equals(data)) {
			current = current.next();
		}

		if(current != null) {
			return current.get();
		}
		return null;
	}

	//Peek last node
	public E peek() {
		return tail.get();
	}
	
	//Return if List Contains Object
	public boolean contains(E data) {
		
		Node<E> current = head;

		while(current != null) {
			if(current.get().equals(data)) {
				return true;
			} else {
				current = current.next();
			}
		}
		return false;
	}

	//Clears Entire List
	public void clear() {
		Node<E> temp = null;
        while (head != null) {
            temp = head.next();
            head = null;
			tail = null;
            head = temp;
        }
	}

	//Returns First Element of List
	public E getFront() {
		return head.get();
	}

	//Return String of All Objects in List
	public String toString() {
		String s = "";

		Node<E> current = head;

		while(current != null) {
			if(current.next() != null) {
				s += current.get().toString() + ", ";
				current = current.next();
			} else {
				s += current.get().toString();
				break;
			}
		}
		return s;
	}

	public String fullString() {
		String s = "";

		Node<E> current = head;
		
		while(current != null) {
			if(current.next() != null) {
				s += current.get().toString() + "\\+";
				current = current.next();
			} else {
				s += current.get().toString();
				break;
			}
		}
		return s;
	}

	//Set Object at Index
	public void set(int index, E data){
        if(index>size()) {
            throw new NullPointerException();
        }

		Node<E> current = head.next();
		for(int i = 0; i<index; i++){
		  	if(current.next() == null){
				return;
		  	}
		  	current = current.next();
		}
		current.setData(data);
	}

	//Swap the Values of two nodes
	public void swap(Node<E> current){
		E temp = current.get();
		current.setData(current.next().get());
		current.next().setData(temp);
	}

	// Sort list by compareTo
	public void sort() {

		boolean sorted = false;
		while(!sorted) {
			sorted = true;
			Node<E> current = head;
			int i=0;

			while(i<size()-1) {
				if(current.next() != null && current.get().compareTo(current.next().get()) > 0) {
					sorted = false;
					swap(current);
				}
				current = current.next();
				i++;
			}
		}
	}
	
	//Reverse List
	public void reverseList() {
		Node<E> current = head;
		while(current.next() != null) {
			current = current.next();
		}
		tail = current;
		Node<E> front = head;
		Node<E> back = tail;
		for(int i=0; i<size()/2; i++) {

			E frontData = front.get();
			E backData = back.get();

			back.setData(frontData);
			front.setData(backData);

			front = front.next();
			back = back.prev();
		}
	}
}