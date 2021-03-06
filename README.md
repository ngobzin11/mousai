mousai
======

This project uses texts from project gutenberg and the Brown NLP Corpus to generate a series of lines that should be semantically correct. A parallel version will follow once the HMMTagger has been fixed and the program has been fully tested. 


	// Scan Books or other texts that do not have a lot of symbols to build vocabulary
	→ Scan input files: 
		→ Extract sentences using end-of-line markers [? . !]
		→ Clean sentences by removing extra spaces among other unnecessary symbols
		→ Build model of sentence lengths
	→ Build corpus vocabulary by splitting sentences and calculating word frequency

	// Initialize Markov Chain based on in input texts
	→ Initialize bigram and unigram prefix dictionary { bi/unigram : Array Of Prefixes }
	→ For every sentence:
		→ Update the dictionary with prefixes and suffixes (duplicates allowed)


	// Initialize + Train POS Tagging VMM Using Brown Corpus
	→ Scan Brown Corpus input files:
		→ Extract word-POS pairs
		→ Extract POS patterns using end-of-sentence markers as delimiters

	// Genetic Algorithm (GA)
	→ Initialize ⇒ Build population of N individuals, with each individual having:
		→ A Sentence generated using the Markov Chain trained by the input texts/books
		→ A POS Tag Sequence for the sentence [Initialized with an empty ArrayList]
		→ A Fitness value of the sentence [Initialized as 0]

	→ Evaluation ⇒ For each individual in the population:
	→ Estimate the individual/sentence’s POS Tag Sequence using the Viterbi 
	Algorithm from the VMM
	→ Calculate the individual’s fitness value based on the POS Tag frequency in the Brown Corpus. As of now there is no smoothing algorithm implemented for patterns that are not recognized. 

		→ Selection ⇒ This is where the elitist part of the algorithm is introduced:
			→ If population has K individuals with the required fitness:
				// This part hasn't been implemented because I haven't found a good enough 
				// threshold to use. Shouldn't be too hard to implement though
				→ BREAK FROM GENETIC ALGORITHM
			→ Else:
				→ Sort the Individuals in terms of fitness
				→ Pick the N / 2 fittest individuals [Fitness > 0]
				→ If individuals with Fitness > 0 are less than N / 2:
					→ Discard Individuals with fitness = 0
					→ Replace them by a new set of randomly generated individuals
					→ GOTO: Evaluation


		→ Reproduction / Crossover ⇒ Do this N / 2 times:
			→ Randomly pick 2 parents from the N / 2 individuals
			→ Duplicate them
			→ Cross the duplicates at a position determined by the VMM training set
			→ Add the duplicates (children) to the population

		→ Mutation ⇒ For every individual in the population:
			→ Generate a random double
			→ If the double is less than the mutation rate
				→ Replace a random word with another word with the same POS tag
			→ GOTO: Evaluation
