package markov;


import java.util.*;

public class MarkovChain {
	
	// The number of words in a prefix
		private static int prefix_len;
		
		// The HashTable that store the prefixes and suffixes
		private static Map<String, ArrayList<String>> statetab;
		
		// List that stores strings that appear right before a stop
		private static Map<String, ArrayList<String>> eol_suffix;
		
		// Random Number generator
		private static Random rand;
		
		public static void init(int pref_len, ArrayList<String> sentences) {
			prefix_len = pref_len;
			statetab = new HashMap<String, ArrayList<String>>();
			eol_suffix = new HashMap<String, ArrayList<String>>();
			rand = new Random();
			
			build(sentences);			
		}

		private static void build(ArrayList<String> sentences) {
			for (String sentence : sentences) {
				String[] words = sentence.split(" ");
				int len = words.length;
				
				if (len < prefix_len)
					continue;
				
				// Set the first prefix to be the empty string's suffix
				String suffix = "";
				for (int i = 0; i < prefix_len; i++) {
					suffix += (i < prefix_len - 1) ? words[i] + " " : words[i];
				}
				add("", suffix);
				
				// Sentence shorter than the suffix length will not be used
				for (int i = 0; i < len - prefix_len; i++) {
					String prefix = "";
					for (int j = 0; j < prefix_len; j++) {
						prefix += (j < prefix_len - 1) ? words[i + j] + " ": words[i + j];
					}
					// Add suffix to state table
					add(prefix, words[i + prefix_len]);
					
					// Append to eol suffix list if it's the last word
					if ((i + prefix_len) == (len - 1)) {
						String last_word = prefix.split(" ")[prefix_len - 1];
						ArrayList<String> suf_lst = (eol_suffix.containsKey(last_word)) ? 
								eol_suffix.get(last_word) : new ArrayList<String>();
						
						suf_lst.add(words[i + prefix_len]);
						eol_suffix.put(last_word, suf_lst);
					}
				}
			}
		}
		
		public static ArrayList<String> nextSentence(int length) {
			// Randomly pick prefix from list of empty string suffixes
			ArrayList<String> prefs = statetab.get("");
			String prefix = prefs.get(rand.nextInt(prefs.size()));
			
			ArrayList<String> sentence = new ArrayList<String>();
			String[] words = prefix.split(" ");
			for (String word : words)
				if ((!word.equals("")) && (!word.equals(" ")))
					sentence.add(word);
			
			String last_word = words[words.length - 1];

			for (int i = words.length - 1; i < length; i++) {
				ArrayList<String> suffixes = ((i == length - 1) && (eol_suffix.containsKey(words[words.length - 2]))) ? 
						eol_suffix.get(words[words.length - 2]) : statetab.get(prefix);
						
				if (suffixes != null)
					last_word = suffixes.get(rand.nextInt(suffixes.size()));
				else
					break;
				
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
			
			// N-word prefix where N > 1 
			if (statetab.containsKey(prefix))
				pref_lst = statetab.get(prefix);

			// Add N-word prefix
			pref_lst.add(suffix);
			statetab.put(prefix, pref_lst);
		}

}
