package org.yoon_technology.data;

public class Queue<T> {
	private Node firstIn;
	private Node lastIn;
	private int n;

	/**
	 * Refer to LICENSE
	 *
	 * @author Jaewan Yun (jay50@pitt.edu)
	 */

	public Queue() {
		n = 0;
	}

	public synchronized T add(T data) {
		if(data == null)
			return null;

		n++;

		if(firstIn == null) {
			firstIn = new Node(data);
			return data;
		} else if(lastIn == null) {
			lastIn = new Node(data);
			firstIn.nextNode = lastIn;
			return data;
		}

		lastIn.nextNode = new Node(data);
		lastIn = lastIn.nextNode;

		return data;
	}

	public synchronized T remove() {
		if(firstIn == null)
			return null;

		T toReturn = firstIn.data;
		firstIn = firstIn.nextNode;

		n--;

		return toReturn;
	}

	public synchronized T peek() {
		if(firstIn != null)
			return firstIn.data;
		return null;
	}

	public synchronized int size() {
		return n;
	}

	public synchronized boolean isEmpty() {
		return firstIn == null;
	}

	private class Node {
		T data;
		Node nextNode;

		Node(T data) {
			this.data = data;
		}
	}

	public static void main(String[] args) {

		Queue<Integer> s = new Queue<>();

		for(int j = 0; j < 100; j++) {
			s.add(new Integer(j));
		}

		System.out.println("SIZE: " + s.size());

		while(!s.isEmpty()) {
			System.out.println(s.remove());
		}

		System.out.println("SIZE: " + s.size());
	}
}
