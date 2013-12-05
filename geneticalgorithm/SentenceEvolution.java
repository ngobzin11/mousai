package geneticalgorithm;


import markov.*;
import objects.*;
import java.util.*;

public class SentenceEvolution {

	private static int pop_size;

	private static Random rand = new Random();

	public static Population evolve(Population population) {
		int index = 0;
		pop_size = population.size();

		// Get the fittest half of the population and shuffle them
		ArrayList<Individual> fittest = population.getFittest(pop_size / 2);

		Collections.shuffle(fittest, rand);

		// Replace any individual that is not fit enough with a newly generated individual
		int count = 0;
		for (Individual individual : fittest) { 
			if ((individual.fitness() == 0.0) || (individual.size() < 4)) {
				fittest.set(index, population.newIndividual());
				count++;
			}
			index++;
		}
		System.err.println("\t<" + count + "> Unfit/Replaced Individuals");

		// Initialize new population
		Population temp_generation = new Population(pop_size);

		// Add the fittest individuals to this population
		for (Individual individual : fittest)
			temp_generation.addIndividual(individual);


		// Generate Children By Crossing Over Parents
		for (int i = 0; i < (pop_size / 4); i++)
			for (Individual individual : crossover(fittest.get(2 * i), fittest.get((2 * i) + 1)))
				temp_generation.addIndividual(individual);


		// Reinitialize population
		population = new Population(pop_size);

		// Mutation Here
		index = 0;
		Individual prev = null;
		for (Individual cur : temp_generation.sort().getSentences()) {
			if (index++ == 0) {
				prev = cur;
				continue;
			}
			population.addIndividual(mutate(prev.sentence(), cur.sentence()));
			prev = cur;
		}

		return population.sort();
	}

	private static ArrayList<Individual> crossover(Individual ind1, Individual ind2) {
		ArrayList<Individual> children = new ArrayList<Individual>();
		Tuple<Integer, Integer> x_y;

		// HMMTagger.locus does not handle individuals without an ArrayList of tags
		// 		Randomly pick a crossover point for both sentences
		if ((ind1.tags() == null || ind2.tags() == null) ||
				(ind1.tags().size() == 0 || ind2.tags().size() == 0))

			x_y = (ind1.size() > ind2.size()) ? 
					new Tuple<Integer, Integer>(rand.nextInt(ind2.size()), rand.nextInt(ind1.size())) :
						new Tuple<Integer, Integer>(rand.nextInt(ind1.size()), rand.nextInt(ind2.size()));
					else
						x_y = HMMTagger.locus(ind1.tags(), ind2.tags());


		ArrayList<String> child1 = new ArrayList<String>();
		ArrayList<String> child2 = new ArrayList<String>();
		ArrayList<String> s_sent;
		ArrayList<String> l_sent;
		if (ind1.size() > ind2.size()) {
			s_sent = ind2.sentence();
			l_sent = ind1.sentence();
		} else {
			s_sent = ind1.sentence();
			l_sent = ind2.sentence();
		}

		// Add Words To First Child
		int i;
		for (i = 0; i <= x_y.first(); i++)
			child1.add(s_sent.get(i));

		for (i = x_y.second(); i < l_sent.size(); i++)
			child1.add(l_sent.get(i));

		// Add Words To Second Child
		for (i = 0; i < x_y.second(); i++)
			child2.add(l_sent.get(i));

		for (i = x_y.first() + 1; i < s_sent.size(); i++)
			child2.add(s_sent.get(i));

		Individual ch1 = new Individual();
		if (child1.size() == 0 || child1 == null)
			child1 = MarkovChain.nextSentence(Style.nextSentenceLength());

		ch1.generate(child1);
		children.add(ch1);

		Individual ch2 = new Individual();
		if (child2.size() == 0 || child2 == null)
			child2 = MarkovChain.nextSentence(Style.nextSentenceLength());
		ch2.generate(child2);
		children.add(ch2);


		return children;
	}

	private static Individual mutate(ArrayList<String> prev, ArrayList<String> cur) {
		Individual individual = new Individual();
		if (rand.nextDouble() <= wordSimilarity(prev, cur))
			prev.set(rand.nextInt(prev.size()), Style.randomWord());

		individual.generate(prev);
		return individual;
	}

	private static double wordSimilarity(ArrayList<String> prev, ArrayList<String> cur) {
		int same = 0;
		for (String word1 : prev)
			for (String word2 : cur)
				if (word1.equals(word2))
					same++;

		return same / (1.0 * Math.max(prev.size(), cur.size()));
	}

}
