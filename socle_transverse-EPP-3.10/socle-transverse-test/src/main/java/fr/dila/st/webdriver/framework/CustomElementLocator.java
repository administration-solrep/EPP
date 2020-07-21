package fr.dila.st.webdriver.framework;

import java.lang.reflect.Field;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.ElementLocator;

public class CustomElementLocator implements ElementLocator {

	private final SearchContext	searchContext;

	private final By			by;

	private final boolean		shouldCache;

	private WebElement			cachedElement;

	private List<WebElement>	cachedElementList;

	public CustomElementLocator(SearchContext searchContext, Field field) {
		this.searchContext = searchContext;
		Annotations annotations = new Annotations(field);
		shouldCache = annotations.isLookupCached();
		CustomFindBy findBy = field.getAnnotation(CustomFindBy.class);
		if (findBy != null) {
			by = buildByFromRepFindBy(findBy, field);
		} else {
			by = annotations.buildBy();
		}
	}

	private By buildByFromRepFindBy(CustomFindBy findBy, Field field) {
		assertValidRepFindBy(findBy);

		CustomHow how = findBy.how();
		String using = findBy.using();

		switch (how) {
			case PARTIAL_SPAN_TEXT:
				return STBy.partialSpanText(using);

			case LABEL_TEXT:
				return STBy.labelText(using);

			case LABEL_ON_NUXEO_LAYOUT:
				return STBy.labelOnNuxeoLayoutForInput(using);

			case NODE_NAME_FOR_TOGGLE_BTN_ORGA:
				return STBy.nodeNameForToggleBtnOrga(using);

			case INPUT_VALUE:
				return STBy.inputValue(using);
			default:
				throw new IllegalArgumentException("Cannot determine how to locate element " + field);
		}
	}

	private void assertValidRepFindBy(CustomFindBy findBy) {
		if (findBy.how() != null) {
			if (findBy.using() == null) {
				throw new IllegalArgumentException("If you set the 'how' property, you must also set 'using'");
			}
		}
	}

	/**
	 * Find the element.
	 */
	@Override
	public WebElement findElement() {
		if (cachedElement != null && shouldCache) {
			return cachedElement;
		}

		WebElement element = searchContext.findElement(by);
		if (shouldCache) {
			cachedElement = element;
		}

		return element;
	}

	/**
	 * Find the element list.
	 */
	public List<WebElement> findElements() {
		if (cachedElementList != null && shouldCache) {
			return cachedElementList;
		}

		List<WebElement> elements = searchContext.findElements(by);
		if (shouldCache) {
			cachedElementList = elements;
		}

		return elements;
	}

}
