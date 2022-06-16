package fr.sword.naiad.nuxeo.commons.test.runner;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * FeaturesRunner exécutant les tests dans l'ordre, d'après le nom de la méthode.
 * 
 * @author fmh
 */
public class OrderedRunner extends FeaturesRunner {
	/**
	 * Construit le runner à partir de la classe à exécuter.
	 */
	public OrderedRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

	/**
	 * Retourne la liste triée des méthodes portant un test à exécuter.
	 * 
	 * @return Liste triée des méthodes.
	 */
	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		List<FrameworkMethod> list = super.computeTestMethods();

		Collections.sort(list, new FrameworkMethodComparator());

		return list;
	}

	/**
	 * Comparateur de FrameworkMethod, se basant sur le nom des méthodes. Celles-ci sont triées alphabétiquement.
	 * 
	 * @author fmh
	 */
	private static class FrameworkMethodComparator implements Comparator<FrameworkMethod>, Serializable {
		/**
		 * Serial UID généré.
		 */
		private static final long serialVersionUID = 3098413053684164798L;

		/**
		 * Constructeur par défaut.
		 */
		public FrameworkMethodComparator() {
			super();
		}

		/**
		 * Compare deux FrameworkMethod.
		 * 
		 * @param fm1
		 *            Première méthode.
		 * @param fm2
		 *            Deuxième méthode.
		 * @return -1 si le nom de fm1 est inférieur au nom de fm2, 0 s'il est égal, 1 s'il est supérieur.
		 */
		@Override
		public int compare(FrameworkMethod fm1, FrameworkMethod fm2) {
			return fm1.getName().compareTo(fm2.getName());
		}
	}
}
