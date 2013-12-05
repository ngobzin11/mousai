package geneticalgorithm;


import java.util.Comparator;

public class IndividualComparator implements Comparator<Individual>{

	@Override
	public int compare(Individual ind0, Individual ind1) {
		double dif = ind0.fitness() - ind1.fitness();
		
		if (dif == 0.0)
			return 0;
		
		else if (dif > 0.0) 
			return -1;

		return  1;
	}

}