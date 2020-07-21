package fr.dila.tools.perf;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.security.SecureRandom;

/**
 * Lance un nombre de thread donné chacune faisant un nombre donné de calcul. Le but est de faire monter en charge le
 * CPU afin de tester la réparition de la charge dans le cas de bi-pro. (En lançant le process avec la commande unix
 * taskset)
 * 
 * La fonction main prend paramètre le nombre de thread et le nombre de tour de boucle.
 * 
 * @author spesnel
 * 
 */
public class Cpu {

	public static class CpuThread extends Thread {
		private int		nbLoop;
		private double	cumul	= 0;

		public CpuThread(int nbLoop) {
			this.nbLoop = nbLoop;
		}

		private double compute() {

			// ABI : audit sécurité 2017 , génération nombre aléatoire
			SecureRandom sr = new SecureRandom();
			double x = sr.nextDouble();
			double y = sr.nextDouble();
			// double x = Math.random();
			// double y = Math.random();

			double res = Math.atan2(y, x);

			return Math.sqrt(res);
		}

		public void run() {
			int i = 0;

			while (i < nbLoop * 1000) {

				cumul += compute();

				i++;
			}
		}

		public double getCumul() {
			return cumul;
		}
	}

	public static void main(String args[]) {
		if (args.length != 2) {
			System.out.println("Usage : <nb thread> <nb loop>");
			System.exit(1);
		}

		final int nbThread = Integer.parseInt(args[0]);
		final int nbLoop = Integer.parseInt(args[1]);

		System.out.println("Execution : " + nbThread + " threads, " + nbLoop + " loops");

		List<CpuThread> threads = new LinkedList<CpuThread>();

		for (int i = 0; i < nbThread; ++i) {
			CpuThread t = new CpuThread(nbLoop);
			threads.add(t);
			t.start();
		}

		Date start = new Date();
		while (!threads.isEmpty()) {
			if (!threads.get(0).isAlive()) {
				threads.remove(0);
			}
		}
		Date end = new Date();
		long delta = end.getTime() - start.getTime();
		System.out.println("execution in " + delta + " ms");

	}
}
