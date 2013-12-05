package markov;

import objects.*;
import java.io.*;
import java.util.*;
import reader.Util;

public class HMMTagger {

	// Folder Containing Brown Corpus Files
	final static File folder = new File("./brown_corpus");

	// Order Specific Tag Co-occurrence: Forward-Slash (/) Separated Tags <BI-GRAMS> 
	private static Map<String, Integer> bigrams;

	// Tag Order and Count
	private static Map<String, Tuple<Integer, Integer>> tag_count;

	// Sequence Of Tags In A Given Sentence
	private static Map<String, Integer> tag_seq;

	// Word-Tag Co-occurrence Count
	private static Map<String, Integer> wt_count;

	// Tags That Can Be Associated With A Given Word
	private static Map<String, Tuple<Integer, ArrayList<String>>> word_tags;

	// Number of States / Tags
	private static int num_states;

	// Number of Unique Words
	private static int num_words;

	// The Transition Table
	private static double[][] trans_tab;

	// The Emission Table [Tags][Words]
	private static double[][] emis_tab;

	public static void init() {		
		// Read Data From Brown Corpus
		System.err.println("\nLoading Brown Corpus...");
		load();

		// Train
		System.err.println("\nTraining Brown Corpus...");
		train();		
	}

	/* 
	 * Reads the Training dataset, Brown Corpus, and updates the following:
	 * 	- Tag Co-occurrences
	 *	- Tag Count
	 *	- Tag Sequence
	 *	- Word-Tag Co-occurrence Count
	 */ 
	private static void load() {
		bigrams = new HashMap<String, Integer>();
		tag_count = new HashMap<String, Tuple<Integer, Integer>>();
		tag_seq = new HashMap<String, Integer>();
		wt_count = new HashMap<String, Integer>();
		word_tags = new HashMap<String, Tuple<Integer, ArrayList<String>>>();

		BufferedReader reader = null;

		// The Unique Tag Counter Used For Tag Positions
		int unique_tag = 0;

		// The Unique Word Counter Used For Word Positions
		int unique_word = 0;

		for (final File file : folder.listFiles()) {
			try {

				// BegginingOfLine
				boolean bol = true;

				// Previous Tag: Default <BOL> - Beginning Of Line Marker
				String prev_tag = "BOL";

				// The last sentence <Contains POS Only Tags>
				String sentence = "";

				// Counter to keep track of bi-grams
				int counter = 1;

				String line = null;
				reader = new BufferedReader(new FileReader(file));

				while ((line = reader.readLine()) != null) {
					String[] words = line.toLowerCase().split(" ");

					for (String pair : words) {
						String[] wt = pair.split("/");

						// Somehow there are a lot of blank spaces
						if (wt.length != 2)
							continue;

						String tag = Util.clean(wt[1].toUpperCase());
						String word = Util.clean(wt[0]);
						pair = word + "/" + tag;

						// Update Word-Tag Count
						wt_count.put(pair, wt_count.containsKey(pair) ? wt_count.get(pair) + 1 : 1);

						// Update Word's Tags
						if (wt_count.get(pair) == 1) {
							Tuple<Integer, ArrayList<String>> w_pos_tags = word_tags.containsKey(word) ? word_tags.get(word) : 
								new Tuple<Integer, ArrayList<String>>(unique_word++, new ArrayList<String>());
							w_pos_tags.second().add(tag);
							word_tags.put(word, w_pos_tags);
						}

						// If BOL
						if (tag.equals(".")) {
							tag_seq.put(sentence, tag_seq.containsKey(tag) ? tag_seq.get(tag) + 1 : 1);

							sentence = "";
							prev_tag = "BOL";
							bol = true;

						} else {
							// BIGRAM
							if (counter % 2 == 0) {
								String bigram = prev_tag + "/" + tag;
								bigrams.put(bigram, bigrams.containsKey(bigram) ? bigrams.get(bigram) + 1 : 1);
							}

							// Update Tag Count
							Tuple<Integer, Integer> tc = tag_count.containsKey(tag) ? tag_count.get(tag) : 
								new Tuple<Integer, Integer>(unique_tag++, 0);
							tag_count.put(tag, new Tuple<Integer, Integer>(tc.first(), tc.second() + 1));

							prev_tag = tag;
							sentence += bol ? tag : "/" + tag;
							bol = false;
						}
						counter++;
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// Update the number of states
		num_states = unique_tag;

		// Update the number of words
		num_words = word_tags.size();
	}

	public static Tuple<Double, ArrayList<String>> viterbi(ArrayList<String> sentence) {
		int sen_size = sentence.size();
		double[][] trellis = new double[num_states][sen_size];
		int[][] backpointer = new int[num_states][sen_size];

		// Initialize trellis
		for(int i = 0; i < num_states; i++)
			for(int j = 0; j < sen_size; j++)
				// Will -1 do the trick
				trellis[i][j] = Double.NEGATIVE_INFINITY;


		//Initialize First trellis cell
		if (!word_tags.containsKey(sentence.get(0)))
			return new Tuple<Double, ArrayList<String>>(0.0, new ArrayList<String>());

		trellis[tag_count.get(word_tags.get(sentence.get(0)).second().get(0)).first()][0] = 0.0;

		String[] tag_lst = new String[num_states];

		// Fill rest of trellis
		for (int i = 1; i < sen_size; i++) {
			String word = sentence.get(i);
			String prev_word = sentence.get(i - 1);

			// Get allowed tags for word
			ArrayList<String> w_tags = word_tags.containsKey(word) ? 
				word_tags.get(word).second() : null;

			// Get allowed tags for prev_word
			ArrayList<String> pw_tags = word_tags.containsKey(prev_word) ? 
				word_tags.get(prev_word).second() : null;					

			/* Smoothing not implemented yet so just exit if a word is not recognized */ 
			if (w_tags == null || pw_tags == null)
				return new Tuple<Double, ArrayList<String>>(0.0, new ArrayList<String>());

			int w_pos = word_tags.get(word).first();

			// Word's allowed tags 
			for (String tag: w_tags) {
				int c_pos = tag_count.get(tag).first();
				tag_lst[c_pos] = tag;

				// Previous word's allowed tags
				for (String prev_tag : pw_tags) {
					int p_pos = tag_count.get(prev_tag).first();
					tag_lst[p_pos] = prev_tag;

					/*
					 * Sequence Probability 
					 * 		<mu> = trellis[p_pos][i - 1] * p(prev_tag, tag) * p(tag, word)
					 */
					double mu = trellis[p_pos][i - 1] * trans_tab[p_pos][c_pos] * emis_tab[c_pos][w_pos];

					// We want to get the highest probability, so... 
					if (mu > trellis[c_pos][i]) {
						trellis[c_pos][i] = mu;
						backpointer[c_pos][i] = p_pos;
					}
				}
			}
		}

		int[] tags = new int[sen_size];
		tags[sen_size - 1] = tag_count.get(word_tags.get(sentence.get(sen_size - 1)).second().get(0)).first();
		for (int i = sen_size - 1; i > 0; i--) {
			tags[i - 1] = backpointer[tags[i]][i];
		}

		ArrayList<String> t_seq = new ArrayList<String>();
		String tag_str = "BOL/";
		for (int i = 0; i < sen_size; i++) {
			t_seq.add(tag_lst[tags[i]]);
			tag_str += (i < sen_size - 1) ? tag_lst[tags[i]] + "/" : tag_lst[tags[i]];
		}
		double maxp = p(t_seq);

		// Weight the probability by how ever many times the given tag sequence 
		// 	appears in the training data
		maxp *= tag_seq.containsKey(tag_str) ? tag_seq.get(tag_str): 1.0;

		return new Tuple<Double, ArrayList<String>>(maxp, t_seq);
	}

	// Get The Optimal CrossOver Locus For Two Sentences
	public static Tuple<Integer, Integer> locus(ArrayList<String> first, ArrayList<String> second) {
		int min = Math.min(first.size(), second.size());
		int x = 0;
		int y = 0;
		double pmax = 0.0;

		for (int i = 0; i < min; i++) {
			for (int j = 0; j < min; j++) {				
				double p = trans_tab[tag_count.get(first.get(i)).first()][tag_count.get(second.get(j)).first()];
				if (p > pmax) {
					x = i;
					y = j;
					pmax = p;
				}
			}
		}

		return new Tuple<Integer, Integer>(x, y);
	}

	// Calculate The Probability Of A Sequence Occurring
	private static double p(ArrayList<String> tags) {
		double prob = 1.0;

		for (int i = 1; i < tags.size(); i++)
			prob *= trans_tab[tag_count.get(tags.get(i - 1)).first()][tag_count.get(tags.get(i)).first()];

		return prob;
	}

	// Trains the POS tagging on test data: Brown Corpus
	private static void train() {
		// Get Tags: Count and Order
		Set<String> tags = tag_count.keySet();

		// Initiate Transition Table
		trans_tab = new double[num_states][num_states];

		// Initiate Emission Table [Tags][Words]
		emis_tab = new double[num_states][num_words];

		// Update Transition Table
		for (String tag_j : tags) {
			Tuple<Integer, Integer> t_j = tag_count.get(tag_j);

			for (String tag_k : tags) {
				Tuple<Integer, Integer> t_k = tag_count.get(tag_k);

				String bigram = tag_j + "/" + tag_k;
				trans_tab[t_j.first()][t_k.first()] = bigrams.containsKey(bigram) ? 
						(bigrams.get(bigram) * 1.0) / t_j.second() : 0.0;
			}
		}

		// Update Emission Table
		for (String tag_j : tags) {
			Tuple<Integer, Integer> t_j = tag_count.get(tag_j);

			for (String word : word_tags.keySet()) {
				String wt = word + "/" + tag_j;
				emis_tab[t_j.first()][word_tags.get(word).first()] = wt_count.containsKey(wt) ? 
						(wt_count.get(wt) * 1.0) / t_j.second() : 0.0;
			}
		}
	}
}
