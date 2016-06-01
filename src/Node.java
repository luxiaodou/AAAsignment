import java.util.HashSet;

/**
 * Created by luxia on 2016/5/28.
 */
public class Node {
	double pheromone;
	int degree;
	HashSet<Integer> neighbors;
	double ita;
	double p;

	public Node() {
		pheromone = 1;
		neighbors = new HashSet<>();
		degree = 0;
		ita = 0;
		p = 0;
	}

	public void updateDegree() {
		degree = neighbors.size();
	}

	public void addNei(int neiID) {
		neighbors.add(neiID);
	}

	public void calIta() {
		ita = MaxClique.Q * degree / MaxClique.MAXDEGREE;
	}

	public void calP(double sump) {
		p = Math.pow(pheromone,MaxClique.alpha) * Math.pow(ita,MaxClique.beta) / sump;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
