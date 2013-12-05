package markov;

import objects.*;
import reader.*;
import java.util.*;

public class Style {
	
	// Unique list of words used by the author and their frequencies 
	private static Map<String, Integer> vocabulary;
	
	// Sentence Length Distribution
	private static SentenceContainer sentence_data;
	
	private static ArrayList<String> word_set;
	private static ArrayList<Integer> weight_set;
	
	private static Random rand;
	
	public static void generate(ArrayList<String> sentences) {
		vocabulary = new HashMap<String, Integer>();
		ArrayList<Tuple<Integer, Integer>> lengths = new ArrayList<Tuple<Integer, Integer>>();
		int s_count = 0;
		for (String sentence : sentences) {
			String[] words = sentence.split(" ");
			
			// Add number of words in sentence
			lengths.add(new Tuple<Integer, Integer>(s_count++, words.length));
			
			for (String word : words) {
				word = Util.strip(word.toLowerCase());
				vocabulary.put(word, vocabulary.containsKey(word) ? vocabulary.get(word) + 1 : 1);
			}
		}
		sentence_data = new SentenceContainer(lengths);
		
		// Initialize
		rand = new Random();
		wordWeightSet();
	}
	
	public static int nextSentenceLength() {
		int min = (int)Math.round(Style.sentence_data.mean() - (2 * Style.sentence_data.sd()));
		int max = (int)Math.round(Style.sentence_data.mean() + Style.sentence_data.sd());
		int len = min + rand.nextInt(max - min);
		return (len > 3) ? len : (int)Math.round(Style.sentence_data.mean());
	}
	
	public static String randomWord() {
		int idx = 0;
		int prob = Math.abs(rand.nextInt(weight_set.size()));
		int acc_prob = 0;
		
		while (acc_prob < prob)
			acc_prob += weight_set.get(idx++);
		
		return word_set.get(idx - 1);
	}
	
	private static void wordWeightSet() {
		word_set = new ArrayList<String>();
		weight_set = new ArrayList<Integer>();
		int acc_prob = 0;

		for (Map.Entry<String, Integer> entry : Style.vocabulary.entrySet()) {
			acc_prob += entry.getValue();
			word_set.add(entry.getKey());
			weight_set.add(acc_prob);
		}
	}

}
