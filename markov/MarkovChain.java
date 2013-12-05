package markov;


import java.util.*;

public class MarkovChain {
	
	// The number of words in a prefix
		private static int prefix_len;
		
		// The HashTable that store the prefixes and suffixes
		private static Map<String, ArrayList<String>> statetab;
		
		// HashTable that stores prefixes and the number of times they appear
		private static ArrayList<String> prefix_list;
		
		// Random Number generator
		private static Random rand;
		
		public static void init(int pref_len, ArrayList<String> sentences) {
			prefix_len = pref_len;
			statetab = new HashMap<String, ArrayList<String>>();
			prefix_list = new ArrayList<String>();
			rand = new Random();
			
			build(sentences);			
		}

		private static void build(ArrayList<String> sentences) {
			for (String sentence : sentences) {
				String[] words = sentence.split(" ");
				int len = words.length;
				
				// Sentence shorter than the suffix length will not be used
				for (int i = 0; i < len - prefix_len; i++) {
					String prefix = "";
					for (int j = 0; j < prefix_len; j++) {
						prefix += (j < prefix_len - 1) ? words[i + j] + " ": words[i + j];
					}
					// Add suffix to state table
					add(prefix, words[i + prefix_len]);
					
					// Append to prefix list
					prefix_list.add(prefix);
				}
			}
		}
		
		public static ArrayList<String> nextSentence(int length) {
			// Randomly pick prefix from list
			String prefix = prefix_list.get(rand.nextInt(prefix_list.size()));
			
			ArrayList<String> sentence = new ArrayList<String>();
			String[] words = prefix.split(" ");
			for (String word : words)
				if ((!word.equals("")) && (!word.equals(" ")))
					sentence.add(word);
			
			String last_word = words[words.length - 1];

			for (int i = words.length - 1; i < length; i++) {
				ArrayList<String> suffixes = statetab.get(prefix);
				
				if (suffixes != null) {
					last_word = suffixes.get(rand.nextInt(suffixes.size()));
					
				} else {
					// Pick a word based on a one-word prefix
					suffixes = statetab.get(last_word);
					last_word = (suffixes != null) ? 
						suffixes.get(rand.nextInt(suffixes.size())) :
							Style.randomWord();
				}
				sentence.add(last_word);
				
				// Update prefix
				prefix = updatePrefix(sentence);
			}
			return sentence;
		}
		
		private static String updatePrefix(ArrayList<String> sentence) {
			String prefix = "";
			int len = sentence.size();
			
			for (int i = len - prefix_len; i < len; i++)
				prefix += (i < len - 1) ? sentence.get(i) + " " : sentence.get(i);
			
			return prefix;
		}

		private static void add(String prefix, String suffix) {
			ArrayList<String> pref_lst = new ArrayList<String>();
			ArrayList<String> word_lst = new ArrayList<String>();
			
			// N-word prefix where N > 1 
			if (statetab.containsKey(prefix))
				pref_lst = statetab.get(prefix);

			// Add N-word prefix
			pref_lst.add(suffix);
			statetab.put(prefix, pref_lst);
			
			// 1 word prefix as fall back
			if (prefix_len > 1) {
				String[] words = prefix.split(" ");
				String word = words[prefix_len - 1];
				if (!word.equals(" ")) {
					if (statetab.containsKey(word))
						word_lst = statetab.get(prefix);
						
					// Add 1 word prefix
					word_lst.add(suffix);
					statetab.put(word, word_lst);
				}
			}
		}

}
