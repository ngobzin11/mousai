package run;


import geneticalgorithm.*;
import markov.*;
import reader.*;
import java.util.*;


public class Main {

	private static final int pop_size = 300;

	public static void main(String[] args) {

		System.err.println("Reading Books...");
		ArrayList<String> sentences = FileBuffer.read();
		
		System.err.println("\nGenerating Sentence Style...\n");
		Style.generate(sentences);

		System.err.println("\nInitializing Markov Chains...\n");
		// Initialize Markov Chain
		MarkovChain.init(2, sentences);

		// Initialize Tagger
		HMMTagger.init();

		System.err.println("\nGenerating Population <0> <Size - " + pop_size + ">...\n");
		Population population = new Population(pop_size, true);
		System.err.println("\nPopulation Fitness " + population.meanFitness() + "...\n");

		for (int i = 0; i < 5; i++) {
			System.err.println("\nEvolving Population <" + 0 + ">...\n");
			population = SentenceEvolution.evolve(population);
			System.err.println("\nPopulation Fitness " + population.meanFitness() + "...\n");
		}

		int max = 100;
		for (Individual individual : population.getSentences()) {
			if (individual.size() > 4 && individual.fitness() > 0.0) {
				System.out.println(individual.toString());
				max--;
			}
			if (max == 0)
				break;
		}

	}

}
