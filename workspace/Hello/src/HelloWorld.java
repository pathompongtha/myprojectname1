import static java.lang.Math.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

public class HelloWorld {
	static final double pi = 3.14;

	public static void main(String[] args) {
		// boolean on = true;
		// double x = 1;
		// String s = "hello";
		// s += " world";
		// System.out.println(s + " world");
		// System.out.println(s);
		// sqrt(1);
		// s = String.format("ave = %3.2f", x);
		Scanner sc = new Scanner(System.in);
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("filename.txt"));
		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
		}
		
		Animal noName = new Animal();
		noName.speak();
		Dog snoopy = new Dog();
		snoopy.speak();
		snoopy.doSomething();
		snoopy.doSomething();
		snoopy.doSomething();
		snoopy.doSomething();
		System.out.println(snoopy.legs);
		Cat felix = new Cat();
		
	}
	
	static class NatCas extends Animal {
		void speak() {
			System.out.println("aoidhfioahdfhsadhfkasdsnckshfuhwehfdsdnfklskdfhe");
		}
	}
	
	static class Tiger extends Cat {
		
	}
	
	static class Cat extends Dog{
		void speak(String msg) {
			System.out.println("Meow "+msg);
		}
	}
	
	static class Animal {
		int legs;
		
		void speak() {
			System.out.println("...");
		}
		
		void doSomething() {
			legs++;
		}
	}
	
	static class Dog extends Animal {
		
		Dog() {
			legs = 4;
		}
		
		void speak() {
			System.out.println("Woof");
		}
	}

	static class Node {
		Node left, right;
		int key, val;

		Node(int key, int val) {
			this.key = key;
			this.val = val;
			left = null;
			right = null;
		}

		void add(int x) {
			if (x < this.val) {
				if (this.left == null)
					this.left = new Node(x, x);
				else
					(this.left).add(x);
			} else if (x > this.val) {
				if (this.right == null) {
					this.right = new Node(x, x);
				} else {
					this.right.add(x);
				}
			} else { // equal
				// ignore
			}
		}

		boolean search(int x) {
			if (x < this.val) {
				if (this.left == null)
					return false;
				else
					return this.left.search(x);
			} else if (x > this.val) {
				if (this.right == null)
					return false;
				else
					return this.right.search(x);
			} else { // equal
				return true;
			}
		}
	}
}
