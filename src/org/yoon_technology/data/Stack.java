package org.yoon_technology.data;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class Stack<T> {
	private Node top;

	public Stack() {}

	public void push(T data) {
		Node temp = new Node(data);
		temp.bottomNode = top;
		top = temp;
	}

	public T pop() {
		if(top != null) {
			T data = top.data;
			top = top.bottomNode;
			return data;
		}

		return null;
	}

	public T peek() {
		if(top == null)
			return null;
		return top.data;
	}

	public boolean isEmpty() {
		return top == null;
	}

	private class Node {
		T data;
		Node bottomNode;

		public Node(T data) {
			this.data = data;
		}
	}

	//	public static void main(String[] args) {
	//		Stack<Integer> s = new Stack<>();
	//		for(int j = 0 ; j < 100 ; j++) {
	//			s.push(new Integer(j));
	//		}
	//
	//		while(!s.isEmpty()) {
	//			System.out.println(s.pop());
	//		}
	//	}
}
