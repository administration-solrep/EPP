package fr.dila.st.ui.jaxrs.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.inject.Injectable;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.jaxrs.provider.bean.ArrayFromQuery;
import fr.dila.st.ui.jaxrs.provider.bean.BeanNotOK;
import fr.dila.st.ui.jaxrs.provider.bean.BeanOkWithoutAnnotation;
import fr.dila.st.ui.jaxrs.provider.bean.ComplexeBean;
import fr.dila.st.ui.jaxrs.provider.bean.InitBean;
import fr.dila.st.ui.jaxrs.provider.bean.MultipleAnnotationForm;
import fr.dila.st.ui.jaxrs.provider.bean.ParamFromForm;
import fr.dila.st.ui.jaxrs.provider.bean.SimpleWithDefaultFromQuery;
import fr.dila.st.ui.jaxrs.provider.bean.SimpleWithoutDefaultFromQuery;
import fr.dila.st.ui.jaxrs.provider.bean.TestBean;
import fr.dila.st.ui.jaxrs.provider.bean.TypeConversionBean;
import fr.dila.st.ui.jaxrs.provider.bean.WithoutConstructorBean;
import fr.dila.st.ui.jaxrs.provider.bean.WithoutGetterBean;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.NuxeoException;

public class SwordParamBeanProviderTest {
    private static final String REQUEST_BAD_PARAM_ERROR_MESSAGE = "Requête invalide";

    private SwordParamBeanProvider provider;

    @Mock
    private HttpContext contexte;

    @Mock
    private HttpRequestContext request;

    @Mock
    private ComponentContext component;

    @Mock
    private SwBeanParam annot;

    @Mock
    private Parameter param;

    @Before
    public void before() {
        initMocks(this);

        provider = new SwordParamBeanProvider(contexte);
    }

    @Test
    public void testSimpleParam() {
        //Instanciation des mock
        doReturn(SimpleWithoutDefaultFromQuery.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        MultivaluedMapImpl map = new MultivaluedMapImpl();
        map.add("questionOrder", SortOrder.DESC.getValue());
        when(request.getQueryParameters()).thenReturn(map);

        //Instanciation du bean de retour
        //Vérification valeur par defaut
        SimpleWithoutDefaultFromQuery bean = new SimpleWithoutDefaultFromQuery();
        assertEquals(SortOrder.ASC, bean.getQuestionOrder());
        assertEquals("1", bean.getPage());
        assertEquals(true, bean.getValid());
        assertEquals(new GregorianCalendar(2020, 11, 12), bean.getDate());

        //Appel du bean provider
        //Vérification des valeurs injectées avec un simple paramètre et les autres à null
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertNotNull(obj);
        bean = (SimpleWithoutDefaultFromQuery) obj.getValue();
        assertEquals(SortOrder.DESC, bean.getQuestionOrder());
        assertEquals(null, bean.getPage());
        assertEquals(null, bean.getValid());
        assertEquals(null, bean.getDate());
    }

    @Test
    public void testDefaultParam() {
        //Instanciation des mock
        doReturn(SimpleWithDefaultFromQuery.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        MultivaluedMapImpl map = new MultivaluedMapImpl();
        when(request.getQueryParameters()).thenReturn(map);

        //Instanciation du bean de retour
        //Vérification valeur par defaut
        SimpleWithDefaultFromQuery bean = new SimpleWithDefaultFromQuery();
        assertEquals(SortOrder.ASC, bean.getQuestionOrder());
        assertEquals("1", bean.getPage());
        assertEquals(true, bean.getValid());
        assertEquals(new GregorianCalendar(2020, 11, 12), bean.getDate());

        //Appel du bean provider
        //Vérification des valeurs injectées avec un simple paramètre et les autres à null
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertNotNull(obj);
        bean = (SimpleWithDefaultFromQuery) obj.getValue();
        assertEquals(SortOrder.ASC, bean.getQuestionOrder());
        assertEquals("1", bean.getPage());
        assertEquals(true, bean.getValid());
        assertEquals(new GregorianCalendar(2020, 11, 12), bean.getDate());
    }

    @Test
    public void testArrayParam() {
        //Instanciation des mock
        doReturn(ArrayFromQuery.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        MultivaluedMapImpl map = new MultivaluedMapImpl();
        map.add("valeurs", "valeur1");
        map.add("valeurs", "valeur2");
        when(request.getQueryParameters()).thenReturn(map);

        //Instanciation du bean de retour
        //Vérification valeur par defaut
        ArrayFromQuery bean = new ArrayFromQuery();
        assertNotNull(bean.getListe());

        //Appel du bean provider
        //Vérification des valeurs injectées
        //Envoie d'une liste dans la requête
        //L'ensemble des données doivent être présent dans la liste du bean
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertNotNull(obj);
        bean = (ArrayFromQuery) obj.getValue();
        assertNotNull(bean.getListe());
        assertEquals(2, bean.getListe().size());
        assertEquals("valeur1", bean.getListe().get(0));
        assertEquals("valeur2", bean.getListe().get(1));
    }

    @Test
    public void testQueryParam() {
        //Instanciation des mock
        doReturn(SimpleWithoutDefaultFromQuery.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        MultivaluedMapImpl map = new MultivaluedMapImpl();
        map.add("question", "valeur1");
        when(request.getQueryParameters()).thenReturn(map);

        //Appel du bean provider
        //Vérification que la valeur de type string a été injectée
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertNotNull(obj);
        SimpleWithoutDefaultFromQuery bean = (SimpleWithoutDefaultFromQuery) obj.getValue();
        assertEquals("valeur1", bean.getQuestion());
    }

    @Test
    public void testQueryScope() {
        assertEquals(ComponentScope.PerRequest, provider.getScope());
    }

    @Test
    public void testWithoutGetter() {
        //Instanciation des mock
        doReturn(WithoutGetterBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        MultivaluedMapImpl map = new MultivaluedMapImpl();
        map.add("page", "valeur2");
        when(request.getQueryParameters()).thenReturn(map);

        WithoutGetterBean bean = new WithoutGetterBean();
        assertEquals("1", bean.getPage());
        assertEquals(SortOrder.ASC, bean.getMyQuestion());

        //Appel du bean provider
        //Vérification que l'absence de getter sur question ne pose pas problème
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertNotNull(obj);
        bean = (WithoutGetterBean) obj.getValue();
        assertEquals("valeur2", bean.getPage());

        //N'ayant pas de valeur dans la requête et ne reprenant pas la valeur par defaut, la valeur doit être setté à null.
        assertNull(bean.getMyQuestion());
    }

    @Test
    public void testFormParam() {
        //Instanciation des mock
        doReturn(ParamFromForm.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        Form form = new Form();
        form.add("questionOrder", SortOrder.DESC.getValue());
        form.add("page", "pageForm");
        Map<String, Object> map = new HashMap<>();
        map.put("com.sun.jersey.api.representation.form", form);
        when(contexte.getProperties()).thenReturn(map);

        //Instanciation du bean de retour
        //Vérification valeur par defaut
        ParamFromForm bean = new ParamFromForm();
        assertEquals(SortOrder.ASC, bean.getQuestionOrder());
        assertEquals("1", bean.getPage());

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertNotNull(obj);
        bean = (ParamFromForm) obj.getValue();
        assertEquals(SortOrder.DESC, bean.getQuestionOrder());
        assertEquals("pageForm", bean.getPage());
    }

    @Test
    public void testComplexeBean() {
        //Instanciation des mock
        doReturn(ComplexeBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        Form form = new Form();
        form.add("beanComplexe", "{ \"strValeur\" : \"monString\", \"intVal\" : \"69\"}");
        Map<String, Object> map = new HashMap<>();
        map.put("com.sun.jersey.api.representation.form", form);
        when(contexte.getProperties()).thenReturn(map);

        //Instanciation du bean de retour
        //Vérification valeur par defaut
        ComplexeBean bean = new ComplexeBean();
        assertNull(bean.getBean());
        assertTrue(CollectionUtils.isEmpty(bean.getListe()));

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertNotNull(obj);
        bean = (ComplexeBean) obj.getValue();
        TestBean beanVal = bean.getBean();
        assertNotNull(beanVal);
        assertEquals("monString", beanVal.getStrValeur());
        assertEquals(new Integer(69), beanVal.getIntVal());

        form.add("listComplexe", "{ \"strValeur\" : \"monString01\", \"intVal\" : \"15\"}");
        form.add("listComplexe", "{ \"strValeur\" : \"monString02\", \"intVal\" : \"20\"}");
        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        obj = provider.getInjectable(component, annot, param);
        assertNotNull(obj);
        bean = (ComplexeBean) obj.getValue();
        List<TestBean> beanListe = bean.getListe();
        assertTrue(CollectionUtils.isNotEmpty(beanListe));
        assertEquals(2, beanListe.size());
        assertEquals("monString01", beanListe.get(0).getStrValeur());
        assertEquals(new Integer(15), beanListe.get(0).getIntVal());
    }

    @Test
    public void testWithoutAnnotation() {
        //Instanciation des mock
        doReturn(BeanOkWithoutAnnotation.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        MultivaluedMapImpl map = new MultivaluedMapImpl();
        map.add("question", SortOrder.DESC.getValue());
        map.add("page", "15");
        when(request.getQueryParameters()).thenReturn(map);

        //Instanciation du bean de retour
        //Vérification valeur par defaut
        BeanOkWithoutAnnotation bean = new BeanOkWithoutAnnotation();
        assertEquals(SortOrder.ASC, bean.getQuestionOrder());
        assertEquals("1", bean.getPage());

        //Appel du bean provider
        //Vérification que les valeurs n'ont pas changé car pas d'annotation
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertNotNull(obj);
        bean = (BeanOkWithoutAnnotation) obj.getValue();
        assertEquals(SortOrder.ASC, bean.getQuestionOrder());
        assertEquals("1", bean.getPage());
    }

    @Test
    public void testBeanNotOK() {
        //Instanciation des mock
        doReturn(BeanNotOK.class).when(param).getParameterClass();

        //Appel du bean provider
        //Le bean ne répond pas aux exigences il ne pourra donc pas y avoir de génération du bean
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertNull(obj);
    }

    @Test(expected = NuxeoException.class)
    public void testWithoutGoodConstructor() {
        //Instanciation des mock
        doReturn(WithoutConstructorBean.class).when(param).getParameterClass();

        //Appel du bean provider
        //Le bean ne peut pas être instancier. nous avons donc une exeception.
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        obj.getValue();
    }

    @Test
    public void testMultipleAnnotation() {
        //Utilisation d'un même bean pour 2 types de requêtes différentes

        //Instanciation des mock
        doReturn(MultipleAnnotationForm.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);

        //Instanciation du bean de retour
        //Vérification valeur par defaut
        MultipleAnnotationForm bean = new MultipleAnnotationForm();
        assertEquals(SortOrder.ASC, bean.getQuestion());
        assertEquals("1", bean.getPage());

        //Mock d'une requête en POST
        Form form = new Form();
        form.add("questionForm", SortOrder.DESC.getValue());
        form.add("pageForm", "valeurPageForm");
        Map<String, Object> map = new HashMap<>();
        map.put("com.sun.jersey.api.representation.form", form);
        when(contexte.getProperties()).thenReturn(map);

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertNotNull(obj);
        bean = (MultipleAnnotationForm) obj.getValue();
        assertEquals(SortOrder.DESC, bean.getQuestion());
        assertEquals("valeurPageForm", bean.getPage());

        //Mock d'une requête GET avec le même bean
        MultivaluedMapImpl mapGet = new MultivaluedMapImpl();
        mapGet.add("questionQuery", SortOrder.DESC.getValue());
        mapGet.add("pageQuery", "valeurPageQuery");
        when(request.getQueryParameters()).thenReturn(mapGet);
        when(contexte.getProperties()).thenReturn(new HashMap<>());

        bean = (MultipleAnnotationForm) obj.getValue();
        assertEquals(SortOrder.DESC, bean.getQuestion());
        assertEquals("valeurPageQuery", bean.getPage());
    }

    @Test
    public void testInitMethod() {
        //Instanciation des mock
        doReturn(InitBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);

        //Instanciation du bean de retour
        InitBean bean = new InitBean();
        // Vérification des valeurs par défaut
        assertNull(bean.getNom());
        assertNull(bean.getMin());
        assertNull(bean.getQuestion());
        assertNull(bean.getReponse());

        //Appel du bean provider
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertNotNull(obj);
        // Vérification des valeurs initialisées avec les méthodes annotées par @SwBeanInit
        bean = (InitBean) obj.getValue();
        assertEquals(SortOrder.ASC.getValue(), bean.getNom());
        assertNull(bean.getMin());
        assertEquals(SortOrder.ASC.getValue(), bean.getQuestion());
        assertNull(bean.getReponse());
    }

    @Test
    public void testConvertBooleanValue() {
        //Instanciation des mock
        doReturn(TypeConversionBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        Form form = new Form();
        form.add("booleanValue", "false");
        form.add("requiredValue", "1");
        when(contexte.getProperties()).thenReturn(ImmutableMap.of("com.sun.jersey.api.representation.form", form));

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertThat(obj).isNotNull();

        TypeConversionBean bean = (TypeConversionBean) obj.getValue();

        assertThat(bean.getBooleanValue()).isFalse();
    }

    @Test
    public void testConvertInvalidBooleanValue() {
        //Instanciation des mock
        doReturn(TypeConversionBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        Form form = new Form();
        form.add("booleanValue", "bad value");
        form.add("requiredValue", "1");
        when(contexte.getProperties()).thenReturn(ImmutableMap.of("com.sun.jersey.api.representation.form", form));

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertThat(obj).isNotNull();

        Throwable throwable = catchThrowable(obj::getValue);
        assertThat(throwable)
            .isExactlyInstanceOf(STValidationException.class)
            .hasMessage(REQUEST_BAD_PARAM_ERROR_MESSAGE)
            .hasCauseExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testConvertKeyAndEmptyBooleanValue() {
        //Instanciation des mock
        doReturn(TypeConversionBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        Form form = new Form();
        form.add("booleanValue", null);
        form.add("requiredValue", "1");
        when(contexte.getProperties()).thenReturn(ImmutableMap.of("com.sun.jersey.api.representation.form", form));

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertThat(obj).isNotNull();

        Throwable throwable = catchThrowable(obj::getValue);
        assertThat(throwable)
            .isExactlyInstanceOf(STValidationException.class)
            .hasMessage(REQUEST_BAD_PARAM_ERROR_MESSAGE)
            .hasCauseExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testConvertNullBooleanValue() {
        //Instanciation des mock
        doReturn(TypeConversionBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        Form form = new Form();
        form.add("requiredValue", "1");
        when(contexte.getProperties()).thenReturn(ImmutableMap.of("com.sun.jersey.api.representation.form", form));

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertThat(obj).isNotNull();

        TypeConversionBean bean = (TypeConversionBean) obj.getValue();

        assertThat(bean.getBooleanValue()).isNull();
    }

    @Test
    public void testConvertIntegerValue() {
        int valueToConvert = 1586;

        //Instanciation des mock
        doReturn(TypeConversionBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        Form form = new Form();
        form.add("integerValue", String.valueOf(valueToConvert));
        form.add("requiredValue", "1");
        when(contexte.getProperties()).thenReturn(ImmutableMap.of("com.sun.jersey.api.representation.form", form));

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertThat(obj).isNotNull();

        TypeConversionBean bean = (TypeConversionBean) obj.getValue();

        assertThat(bean.getIntegerValue()).isEqualTo(valueToConvert);
    }

    @Test
    public void testConvertInvalidIntegerValue() {
        //Instanciation des mock
        doReturn(TypeConversionBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        Form form = new Form();
        form.add("integerValue", "bad value");
        form.add("requiredValue", "1");
        when(contexte.getProperties()).thenReturn(ImmutableMap.of("com.sun.jersey.api.representation.form", form));

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertThat(obj).isNotNull();

        Throwable throwable = catchThrowable(obj::getValue);
        assertThat(throwable)
            .isExactlyInstanceOf(STValidationException.class)
            .hasMessage(REQUEST_BAD_PARAM_ERROR_MESSAGE);
    }

    @Test
    public void testConvertKeyAndEmptyIntegerValue() {
        //Instanciation des mock
        doReturn(TypeConversionBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        Form form = new Form();
        form.add("integerValue", null);
        form.add("requiredValue", "1");
        when(contexte.getProperties()).thenReturn(ImmutableMap.of("com.sun.jersey.api.representation.form", form));

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertThat(obj).isNotNull();

        TypeConversionBean bean = (TypeConversionBean) obj.getValue();

        assertThat(bean.getIntegerValue()).isNull();
    }

    @Test
    public void testConvertNullIntegerValue() {
        //Instanciation des mock
        doReturn(TypeConversionBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        Form form = new Form();
        form.add("requiredValue", "1");
        when(contexte.getProperties()).thenReturn(ImmutableMap.of("com.sun.jersey.api.representation.form", form));

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertThat(obj).isNotNull();

        TypeConversionBean bean = (TypeConversionBean) obj.getValue();

        assertThat(bean.getIntegerValue()).isNull();
    }

    @Test
    public void testConvertNullIntegerValueForRequiredField() {
        //Instanciation des mock
        doReturn(TypeConversionBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        when(contexte.getProperties())
            .thenReturn(ImmutableMap.of("com.sun.jersey.api.representation.form", new Form()));

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertThat(obj).isNotNull();

        Throwable throwable = catchThrowable(obj::getValue);
        assertThat(throwable)
            .isExactlyInstanceOf(STValidationException.class)
            .hasMessage(REQUEST_BAD_PARAM_ERROR_MESSAGE);
    }

    @Test
    public void testConvertNullLongValue() {
        //Instanciation des mock
        doReturn(TypeConversionBean.class).when(param).getParameterClass();
        when(contexte.getRequest()).thenReturn(request);
        Form form = new Form();
        form.add("requiredValue", "1");
        when(contexte.getProperties()).thenReturn(ImmutableMap.of("com.sun.jersey.api.representation.form", form));

        //Appel du bean provider
        //Vérification des valeurs injectées via un formulaire en POST
        Injectable<Object> obj = provider.getInjectable(component, annot, param);
        assertThat(obj).isNotNull();

        TypeConversionBean bean = (TypeConversionBean) obj.getValue();

        assertThat(bean.getLongValue()).isNull();
    }
}
