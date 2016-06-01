import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by luxia on 2016/5/28.
 */
public class MaxClique {
	public static final int NodeCount = 4001;
	public static final int BITMAPSIZE = NodeCount / 30 + 1;
	public static final int ANTCOUNT = 100;
	public static final int MAXTIME = 200;
	public static int MAXDEGREE = 0;
	public static final int alpha = 1;
	public static final int beta = 1;
	public static final double rou = 0.95;
	public static final double MAX_PHEROMONE = 4;
	public static final double MIN_PHEROMONE = 0.01;
	public static final int Q = 100;
	public static double q0 = 0.5;
	public static double[] pheromone = new double[NodeCount];
	public static int[] degree = new int[NodeCount];
	public static double[] p = new double[NodeCount];
	public static double[] ita = new double[NodeCount];
	public static int[][] neibor = new int[NodeCount][BITMAPSIZE];

	public static void main(String[] args) {
		/////////////initialize/////////////////////
		//Node[] Nodes = new Node[NodeCount];
		HashSet<Integer> CBest = new HashSet<>();
		for (int i = 0; i < NodeCount; i++) {
			pheromone[i] = 1.0;
		}

		File file = new File("E:\\Lessons\\Java\\AAAsignment\\frb100-40.clq");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			tempString = reader.readLine();
			while ((tempString = reader.readLine()) != null) {
				String[] split = tempString.split(" ");
				int id = Integer.parseInt(split[1]);
				int nei = Integer.parseInt(split[2]);
				int neioff = nei % 30;
				int idoff = id % 30;

				neibor[id][nei / 30] = neibor[id][nei / 30] | (1 << neioff);
				degree[id]++;
				neibor[nei][id / 30] = neibor[nei][id / 30] | (1 << idoff);
				degree[nei]++;
				line++;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i : degree) {
			if (MAXDEGREE < degree[i]) {
				MAXDEGREE = degree[i];
			}
		}

		/////////////////////start////////////////////////
		int time = 0;
		do {
			HashSet<Integer> CBetter = new HashSet<>();
			for (int ant = 0; ant < ANTCOUNT; ant++) {   //for k in 1~n ants do:
				HashSet<Integer> CAnt = new HashSet<>();   //Antbest
				Random rd = new Random();
				int index = rd.nextInt(NodeCount);
				int[] now = new int[BITMAPSIZE];
				CAnt.add(index);
				HashSet<Integer> Candidate;

				do {
					Candidate = new HashSet<>();
					for (int i = 1; i < NodeCount; i++) {
						if (CAnt.contains(i)) {
							continue;
						}
						boolean include = true;
						for (int j = 0; j < BITMAPSIZE; j++) {
							if ((neibor[i][j] & now[j]) != now[j]) {
								include = false;
								break;
							}
						}

						if (include) {
							Candidate.add(i);
						}
					}
					if (Candidate.size() == 0) {
						break;
					}
					///////Calculate p/////////
					double sump = 0;
					double[] temp = new double[NodeCount];
					for (Integer i : Candidate) {
						ita[i] = Q * degree[i] / MAXDEGREE;
						temp[i] = Math.pow(pheromone[i],alpha) * Math.pow(ita[i],beta);
						sump += temp[i];
					}
					for (Integer i : Candidate) {
						p[i] = temp[i] / sump;
					}

					double shuffle = rd.nextDouble();
					double max = 0;
					int pick = 0;
					if (shuffle <= q0) {
						for (Integer i : Candidate) {
							double weight = temp[i];
							if (max < weight) {
								max = weight;
								pick = i;
							}
						}
					}
					else {
						double r = rd.nextDouble();
						double f = 0;
						for (Integer i : Candidate) {
							f += p[i];
							if (f >= r) {
								pick = i;
								break;
							}
						}
					}

					now[pick / 30] = now[pick / 30] | (1 << (pick % 30));
					CAnt.add(pick);
				} while (!Candidate.isEmpty());

				if (CBetter.size() < CAnt.size()) {
					CBetter = CAnt;
				}
			}//End of for
			/////////////Update-pheromone/////////////////

			if (CBest.size() < CBetter.size()) {
				CBest = CBetter;
			}

			for (int i = 0; i < NodeCount; i++) {
				pheromone[i] *= rou;
				if (CBetter.contains(i)) {
					pheromone[i] += (1 / (1 + CBest.size() - CBetter.size()));
				}
				pheromone[i] = pheromone[i] > MAX_PHEROMONE ? MAX_PHEROMONE :
						pheromone[i] < MIN_PHEROMONE ? MIN_PHEROMONE :
								pheromone[i];
			}


			System.out.println("Time" + (time + 1) + " : " + CBetter.size());
			time++;
			q0 = q0 - 0.4 * time / MAXTIME;

			if (time == 50) {
				System.out.println("Wait!");
			}
			///////////Next-generation///////////////////
		} while (time < MAXTIME);

	}
}
