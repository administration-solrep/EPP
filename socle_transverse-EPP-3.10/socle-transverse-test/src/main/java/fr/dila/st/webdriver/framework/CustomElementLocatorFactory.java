package fr.dila.st.webdriver.framework;

import java.lang.reflect.Field;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public final class CustomElementLocatorFactory implements ElementLocatorFactory {
	private final SearchContext	searchContext;

	public CustomElementLocatorFactory(SearchContext searchContext) {
		this.searchContext = searchContext;
	}

	public ElementLocator createLocator(Field field) {
		return new CustomElementLocator(searchContext, field);
	}
}
