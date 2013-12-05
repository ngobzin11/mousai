package objects;

public class Tuple<T1, T2> {
	
	private T1 first;
	private T2 second;
	
	public Tuple(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
	
	public T1 first() {
		return first;
	}
	
	public T2 second() {
		return second;
	}
	
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

}
