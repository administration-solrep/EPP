package fr.dila.st.ui.jaxrs.provider;

import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.LOG_EXCEPTION_TEC;
import static java.util.stream.Collectors.toSet;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.annot.SwBeanInit;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.jaxrs.provider.converters.SwordBooleanConverter;
import fr.dila.st.ui.jaxrs.provider.converters.SwordCalendarConverter;
import fr.dila.st.ui.utils.ValidatorUtils;
import fr.dila.st.ui.validators.annot.SwRequired;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

@Provider
public final class SwordParamBeanProvider implements InjectableProvider<SwBeanParam, Parameter> {
    private static final STLogger LOGGER = STLogFactory.getLog(SwordParamBeanProvider.class);
    private static final String REQUEST_BAD_PARAM_ERROR_MESSAGE = "request.bad.param";

    @Context
    private final HttpContext hc;

    private FormDataMultiPart formMultipart;

    public SwordParamBeanProvider(@Context HttpContext hc) {
        this.hc = hc;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable<Object> getInjectable(ComponentContext ic, final SwBeanParam a, final Parameter c) {
        if (c.getParameterClass().getAnnotation(SwBean.class) == null) {
            return null;
        }

        return () -> {
            try {
                boolean keepDefaultValue = c.getParameterClass().getAnnotation(SwBean.class).keepdefaultValue();
                Object parameterBean = c.getParameterClass().getConstructor().newInstance();

                for (Field field : getFields(parameterBean.getClass())) {
                    //On ne prend pas en compte les champs synthétique (Jacoco)
                    if (!field.isSynthetic()) {
                        Object myValue = null;

                        if (field.isAnnotationPresent(QueryParam.class)) {
                            Object queryValue = getValueFromQuery(hc, field, field.getAnnotation(QueryParam.class));
                            if (queryValue != null) {
                                myValue = queryValue;
                            }
                        }

                        if (field.isAnnotationPresent(FormParam.class)) {
                            Object formValue = getValueFromForm(hc, field, field.getAnnotation(FormParam.class));
                            if (formValue != null) {
                                myValue = formValue;
                            }
                        }

                        if (field.isAnnotationPresent(FormDataParam.class)) {
                            Object formDataValue = getValueFromFormData(
                                hc,
                                field,
                                field.getAnnotation(FormDataParam.class)
                            );
                            if (formDataValue != null) {
                                myValue = formDataValue;
                            }
                        }

                        if (myValue != null || !keepDefaultValue) {
                            BeanUtilsBean bub = getPropertyMapper(myValue);

                            bub.setProperty(parameterBean, field.getName(), myValue);
                        }
                    }
                }
                if (!ValidatorUtils.isValidEntity(parameterBean)) {
                    LOGGER.error(
                        LOG_EXCEPTION_TEC,
                        ResourceHelper.getString(
                            "back.validation.error.invalid.parameter",
                            parameterBean.getClass().getSimpleName()
                        )
                    );
                    throw new STValidationException(REQUEST_BAD_PARAM_ERROR_MESSAGE);
                }
                invokeInitBeanMethods(parameterBean);

                return parameterBean;
            } catch (ReflectiveOperationException e) {
                LOGGER.error(
                    LOG_EXCEPTION_TEC,
                    ResourceHelper.getString("back.validation.error.parameter.conversion", c.getParameterClass())
                );
                throw new STValidationException(REQUEST_BAD_PARAM_ERROR_MESSAGE, e);
            }
        };
    }

    private static void invokeInitBeanMethods(Object parameterBean) {
        getInitBeanMethods(parameterBean.getClass()).forEach(m -> invokeMethod(m, parameterBean));
    }

    private static Set<Method> getInitBeanMethods(Class<?> clazz) {
        Set<Method> initMethods = Stream
            .of(clazz.getDeclaredMethods())
            .filter(m -> m.getAnnotation(SwBeanInit.class) != null)
            .collect(toSet());
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            initMethods.addAll(getInitBeanMethods(superclass));
        }
        return initMethods;
    }

    private static void invokeMethod(Method method, Object obj) {
        try {
            method.invoke(obj);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOGGER.error(
                LOG_EXCEPTION_TEC,
                ResourceHelper.getString(
                    "back.validation.error.method.invocation",
                    method,
                    obj.getClass().getSimpleName()
                )
            );
            throw new STValidationException(REQUEST_BAD_PARAM_ERROR_MESSAGE, e);
        }
    }

    private static BeanUtilsBean getPropertyMapper(Object myValue) {
        Converter calendarConverter = new SwordCalendarConverter();
        Converter booleanConverter = new SwordBooleanConverter(myValue);

        ConvertUtilsBean cub = new ConvertUtilsBean();
        cub.register(calendarConverter, Calendar.class);
        cub.register(calendarConverter, GregorianCalendar.class);
        cub.register(booleanConverter, Boolean.class);
        // surcharge des convertisseurs pour les nombres pour que la valeur par défaut soit null et pas 0
        cub.register(new IntegerConverter(null), Integer.class);
        cub.register(new LongConverter(null), Long.class);

        return new BeanUtilsBean(cub);
    }

    private static Object getValueFromQuery(HttpContext hc, Field myField, QueryParam myAnnot) {
        Object myValue = null;

        if (
            hc.getRequest().getQueryParameters() != null &&
            hc.getRequest().getQueryParameters().containsKey(myAnnot.value())
        ) {
            List<String> paramValues = hc.getRequest().getQueryParameters().get(myAnnot.value());
            myValue = convertValueToPropertyType(myField, paramValues);
        }
        return myValue;
    }

    private static Object getValueFromForm(HttpContext hc, Field myField, FormParam myAnnot) {
        Object myValue = null;
        Form form = (Form) hc.getProperties().get("com.sun.jersey.api.representation.form");
        if (form != null && form.containsKey(myAnnot.value())) {
            List<String> paramValues = form.get(myAnnot.value());
            myValue = convertValueToPropertyType(myField, paramValues);
        }
        return myValue;
    }

    private Object getValueFromFormData(HttpContext hc, Field myField, FormDataParam myAnnot) {
        Object myValue = null;
        if (formMultipart == null) {
            formMultipart = hc.getRequest().getEntity(FormDataMultiPart.class);
        }

        if (formMultipart != null && formMultipart.getFields(myAnnot.value()) != null) {
            List<String> paramValues = new ArrayList<>();
            for (FormDataBodyPart part : formMultipart.getFields(myAnnot.value())) {
                paramValues.add(part.getValue());
            }
            myValue = convertValueToPropertyType(myField, paramValues);
        }
        return myValue;
    }

    private static Object convertValueToPropertyType(Field myField, List<String> values) {
        Object convertedValue = null;
        if (CollectionUtils.isNotEmpty(values)) {
            convertedValue = PropertyTypeConverter.fromField(myField).convertFieldValue(myField, values);
        } else {
            if (myField.getClass().getAnnotation(SwRequired.class) != null) {
                LOGGER.error(
                    LOG_EXCEPTION_TEC,
                    ResourceHelper.getString("back.validation.error.required.field", myField.getName())
                );
                throw new STValidationException(REQUEST_BAD_PARAM_ERROR_MESSAGE);
            }
        }
        return convertedValue;
    }

    private static Field[] getFields(Class<?> parameterBeanClass) {
        return ArrayUtils.addAll(
            parameterBeanClass.getDeclaredFields(),
            getInheritedFields(parameterBeanClass.getSuperclass())
        );
    }

    private static Field[] getInheritedFields(Class<?> parameterBeanSuperclass) {
        Field[] fields = null;

        if (parameterBeanSuperclass != null && !parameterBeanSuperclass.equals(Object.class)) {
            fields = getFields(parameterBeanSuperclass);
        }
        return fields;
    }
}
