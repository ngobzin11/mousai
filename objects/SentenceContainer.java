package objects;

import java.util.*;

public class SentenceContainer {
	
	private double mean;
	private double sd;
	private double median;
	private int max;
	private int min;
	private ArrayList<Tuple<Integer, Integer>> collection;
	
	public SentenceContainer(ArrayList<Tuple<Integer, Integer>> collection) {
		this.collection = collection;
		min = max = 0;
		stats();
	}
	
	private void stats() {
		double sum = 0.0;
		double sum_sq = 0.0;
		int num = 0;
		
		for (Tuple<Integer, Integer> t : collection) {
			if (t.second() > max)
				max = t.second();

			if (t.second() < min)
				min = t.second();
			
			sum += t.second();
			sum_sq += Math.pow(t.second(), 2);
			num++;
		}
		
		mean = sum / num;
		sd = Math.sqrt((sum_sq / num) - Math.pow(mean, 2));
		median = (num % 2 == 0) ? 
				(double) ((collection.get(num / 2).first() + collection.get((num / 2) + 1).first()) / 2) : 
					(double) collection.get(num / 2).first();
	}
	
	public int max() {
		return max;
	}
	
	public int min() {
		return min;
	}
	
	public double mean() {
		return mean;
	}
	
	public double median() {
		return median;
	}
	
	public double sd() {
		return sd;
	}
	
	public ArrayList<Tuple<Integer, Integer>> collection() {
		return collection;
	}

}
