package geneticalgorithm;

import markov.HMMTagger;
import objects.Tuple;
import java.util.ArrayList;

public class Individual {

	// Chromosome
	private ArrayList<String> sentence;

	// Sentence Tags
	private ArrayList<String> tags;

	// Sentence Length
	private int size;

	// Sentence Fitness --> Highest Cosine Similarity to a sentence in the training tests 
	private double fitness;

	/*
	 * Most of the work is done by the population generator  
	 */
	public void generate(ArrayList<String> sentence) {
		// Generate tags and fitness for individual
		Tuple<Double, ArrayList<String>> tag_data = HMMTagger.viterbi(sentence);

		this.fitness = tag_data.first();
		size = sentence.size();
		this.sentence = new ArrayList<String>();
		this.tags = new ArrayList<String>();

		for (String word : sentence)
			this.sentence.add(word);

		for (String word : tag_data.second())
			this.tags.add(word);
	}

	public int size() {
		return size;
	}

	public double fitness() {
		return fitness;
	}

	public ArrayList<String> sentence() {
		return sentence;
	}

	public ArrayList<String> tags() {
		return tags;
	}

	public void replace(int pos, String word) {
		sentence.set(pos, word);
	}

	public String toString() {
		String str = fitness + "\t";
		for (String word : sentence)
			str += " " + word;

		//str += "\tTAGS:\t";

		//for (String word : tags)
		//	str += word + " ";

		return str;
	}

}
