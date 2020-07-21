package fr.dila.st.webdriver.framework;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByXPath;

/**
 * Les locators de la class Réponses
 * 
 * @author jgz
 * 
 */
public abstract class STBy extends By {

	/**
	 * @param partialSpanText
	 *            La valeur du text à rechercher
	 * @return un By qui retourne les tags ayant un span contenant le texte indiqué dans partialSpanText
	 */
	public static By partialSpanText(final String partialSpanText) {
		if (partialSpanText == null) {
			throw new IllegalArgumentException("Ne peut pas rechercher un texte nul");
		}

		return new ByPartialSpanText(partialSpanText);
	}

	public static By partialSpanTextLocal(final String partialSpanText) {
		if (partialSpanText == null) {
			throw new IllegalArgumentException("Ne peut pas rechercher un texte nul");
		}

		return new ByPartialSpanTextLocal(partialSpanText);
	}

	/**
	 * @param labelText
	 *            La valeur du tag label à rechercher
	 * @return un By qui retourne les tags ayant un label contenant le texte indiqué dans labelText
	 */
	public static By labelText(final String labelText) {
		if (labelText == null) {
			throw new IllegalArgumentException("Ne peut pas rechercher un texte nul");
		}

		return new ByLabelText(labelText);
	}

	/**
	 * @param label
	 *            La valeur du label
	 * @return a By qui retourne l'input correspondant au label donné, pour un layout nuxeo standard
	 */
	public static By labelOnNuxeoLayoutForInput(final String label) {
		if (label == null) {
			throw new IllegalArgumentException("Ne peut pas rechercher un label nul");
		}

		return new ByLabelOnNuxeoLayoutForInput(label);
	}

	/**
	 * @param nodeName
	 *            La valeur du noeud
	 * @return a By qui à partir de l'organigramme retourne la case pour déplier ou replier le noeud correspondant à
	 *         nodeName
	 */
	public static By nodeNameForToggleBtnOrga(final String nodeName) {
		if (nodeName == null) {
			throw new IllegalArgumentException("Ne peut pas rechercher un noeud nul");
		}

		return new ByNodeNameForToggleBtnOrga(nodeName);
	}

	/**
	 * Retourne un locator sur un input de value using
	 * 
	 * @param using
	 *            la value de l'input
	 * @return
	 */
	public static By inputValue(String using) {
		if (using == null) {
			throw new IllegalArgumentException("Ne peut pas rechercher un input de valeur nul");
		}

		String xpathexpression = String.format(".//input[@value=\"%s\"]", using);
		return new By.ByXPath(xpathexpression);
	}

	/**
	 * Locator sur un texte de span
	 * 
	 * @author jgz
	 * 
	 */
	public static class ByPartialSpanText extends By {

		private final String	partialSpanText;

		public ByPartialSpanText(String partialSpanText) {
			this.partialSpanText = partialSpanText;
		}

		@Override
		public List<WebElement> findElements(SearchContext context) {
			String using = buildUsing();
			return ((FindsByXPath) context).findElementsByXPath(using);
		}

		@Override
		public WebElement findElement(SearchContext context) {
			String using = buildUsing();
			return ((FindsByXPath) context).findElementByXPath(using);
		}

		private String buildUsing() {
			return String.format("//span[contains(text(),\"%s\")]", partialSpanText);
		}

		@Override
		public String toString() {
			return "By.partialSpanText: " + partialSpanText;
		}
	}

	/**
	 * Locator sur un texte de span sur le noeud courant
	 * 
	 * @author jgz
	 * 
	 */
	// TODO: remplacer le ByPartialSpanText quand le risque de régression aura été écartée
	public static class ByPartialSpanTextLocal extends By {

		private final String	partialSpanText;

		public ByPartialSpanTextLocal(String partialSpanText) {
			this.partialSpanText = partialSpanText;
		}

		@Override
		public List<WebElement> findElements(SearchContext context) {
			String using = buildUsing();
			return ((FindsByXPath) context).findElementsByXPath(using);
		}

		@Override
		public WebElement findElement(SearchContext context) {
			String using = buildUsing();
			return ((FindsByXPath) context).findElementByXPath(using);
		}

		private String buildUsing() {
			return String.format(".//span[contains(text(),\"%s\")]", partialSpanText);
		}

		@Override
		public String toString() {
			return "By.partialSpanText: " + partialSpanText;
		}
	}

	/**
	 * Locator sur un texte de label
	 * 
	 * @author jgz
	 * 
	 */
	public static class ByLabelText extends By {

		private final String	labelText;

		public ByLabelText(String labelText) {
			this.labelText = labelText;
		}

		@Override
		public List<WebElement> findElements(SearchContext context) {
			String using = buildUsing();
			return ((FindsByXPath) context).findElementsByXPath(using);
		}

		@Override
		public WebElement findElement(SearchContext context) {
			String using = buildUsing();
			return ((FindsByXPath) context).findElementByXPath(using);
		}

		private String buildUsing() {
			return String.format("//label[contains(text(),\"%s\")]", labelText);
		}

		@Override
		public String toString() {
			return "By.labelText: " + labelText;
		}
	}

	/**
	 * Locator sur un layout nuxéo : on donne le libellé et le locator retourne l'input correspondant
	 * 
	 * @author jgz
	 * 
	 */
	public static class ByLabelOnNuxeoLayoutForInput extends By {

		private final String	label;

		public ByLabelOnNuxeoLayoutForInput(String label) {
			this.label = label;
		}

		@Override
		public List<WebElement> findElements(SearchContext context) {
			String using = buildUsing();
			return ((FindsByXPath) context).findElementsByXPath(using);
		}

		@Override
		public WebElement findElement(SearchContext context) {
			String using = buildUsing();
			return ((FindsByXPath) context).findElementByXPath(using);
		}

		private String buildUsing() {
			String formatString = "//tr/td/span[contains(text(), \"%s\")]/../following-sibling::td[1]//input[@type='text']";
			return String.format(formatString, this.label);
		}

		@Override
		public String toString() {
			return "By.labelOnNuxeoLayout: " + label;
		}

	}

	/**
	 * Locator sur un bouton toggle de l'organigramme : on donne le libellé et le locator retourne la case à cliquer
	 * pour déplier le noeud correspondant
	 * 
	 * @author jgz
	 * 
	 */
	public static class ByNodeNameForToggleBtnOrga extends By {

		private final String	nodeName;

		public ByNodeNameForToggleBtnOrga(String nodeName) {
			this.nodeName = nodeName;
		}

		@Override
		public List<WebElement> findElements(SearchContext context) {
			String using = buildUsing();
			return ((FindsByXPath) context).findElementsByXPath(using);
		}

		@Override
		public WebElement findElement(SearchContext context) {
			String using = buildUsing();
			return ((FindsByXPath) context).findElementByXPath(using);
		}

		private String buildUsing() {
			String formatString = "//span[contains(text(), \"%s\")]/../preceding-sibling::*//a";
			return String.format(formatString, this.nodeName);
		}

		@Override
		public String toString() {
			return "By.nodeNameForToggleBtnOrga: " + nodeName;
		}

	}

}
