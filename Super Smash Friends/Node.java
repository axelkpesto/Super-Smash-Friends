public class Node<E> {
	
	private E data;
	private Node<E> next;
    private Node<E> prev;
	
	public Node(E data) {
		this.data = data;
		next = null;
        prev = null;
	}

	public E get() {
		return data;
	}

	public void setData(E e) {
		data = e;
	}
	
	public Node<E> next() {
		return next;
	}

	public void setNext(Node<E> e) {
		next = e;
	}

    public Node<E> prev() {
		return prev;
	}

    public void setPrev(Node<E> e) {
		prev = e;
	}

	public Node<E> getNode() {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		@SuppressWarnings("unchecked") E n = (E)o;
		if(this.equals(n)) {
			return true;
		}
		return false;
	}

    public void delete() {
        prev.setNext(next);
        next.setPrev(prev);
    }
	
	public boolean hasNext() {
		if(next().get() == null) {
			return false;
		}
		return true;
	}
	
	public boolean hasPrev() {
		if(prev().get() == null) {
			return false;
		}
		return true;
	}
}