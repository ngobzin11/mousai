package geneticalgorithm;

import markov.*;
import java.util.*;

public class Population {

	// List of individuals (sentences) in this population
	private ArrayList<Individual> individuals;

	// Population Size --> Number of sentences in the population
	private int size;

	public Population(int size, boolean initialize) {
		this.size = size;

		if (initialize)
			generate();
	}

	public Population(int size) {
		this.size = size;
		individuals = new ArrayList<Individual>();
	}

	private void generate() {
		individuals = new ArrayList<Individual>();
		// GaussSelect.init();

		for (int i = 0; i < size; i++) {
			// Generate Sentence And Add It To Population
			Individual ind = new Individual();
			ind.generate(MarkovChain.nextSentence(Style.nextSentenceLength()));
			individuals.add(ind);
		}
	}

	public Individual newIndividual() {		
		Individual ind = new Individual();
		ind.generate(MarkovChain.nextSentence(Style.nextSentenceLength()));
		return ind;
	}

	public void saveIndividual(int index, Individual individual) {
		individuals.set(index, individual);
	}

	public void addIndividual(Individual individual) {
		individuals.add(individual);
	}

	public Population sort() {
		Collections.sort(individuals, new IndividualComparator());
		return this;
	}

	public ArrayList<Individual> getFittest(int n) {
		ArrayList<Individual> fittest = new ArrayList<Individual>();
		Collections.sort(individuals, new IndividualComparator());

		int count = 0;
		for (Individual ind : individuals) {
			if (count++ == n) {
				break;
			}
			fittest.add(ind);
		}
		return fittest;
	}

	public int size() {
		return size;
	}

	public Individual getSentence(int index) {
		return individuals.get(index);
	}

	public ArrayList<Individual> getSentences() {
		return individuals;
	}

	public double meanFitness() {
		double sum = 0.0;
		for (Individual individual : individuals)
			sum += individual.fitness();

		return sum / (size * 1.0);
	}

}
