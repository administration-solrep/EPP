/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Nicolas Chapurlat <nchapurlat@nuxeo.com>
 */

package org.nuxeo.ecm.core.io.registry;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.nuxeo.ecm.core.io.registry.context.RenderingContext;
import org.nuxeo.ecm.core.io.registry.context.RenderingContext.CtxBuilder;
import org.nuxeo.runtime.api.Framework;

/**
 * Quick use of {@link MarshallerRegistry}.
 *
 * @since 7.2
 */
public final class MarshallerHelper {

    /**
     * Just call static methods.
     */
    private MarshallerHelper() {
    }

    private static MarshallerRegistry getService() {
        MarshallerRegistry registry = Framework.getService(MarshallerRegistry.class);
        return registry;
    }

    /**
     * Checks the marshallers isn't null. Throw an exception if it is.
     */
    private static void checkMarshaller(Type type, Marshaller<?> marshaller) {
        if (marshaller == null) {
            throw new MarshallingException("No marshaller found for type " + type.toString());
        }
    }

    /**
     * Convert the given object to json.
     *
     * @param object The object to convert as json.
     * @param ctx May be null - otherwise, use {@link CtxBuilder} to create the context.
     * @return the resulting json.
     * @since 7.2
     */
    public static <T> String objectToJson(T object, RenderingContext ctx) throws IOException {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) object.getClass();
        Writer<T> writer = getService().getWriter(ctx, type, APPLICATION_JSON_TYPE);
        checkMarshaller(type, writer);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.write(object, type, type, APPLICATION_JSON_TYPE, baos);
        return baos.toString();
    }

    /**
     * Convert the given object to json.
     * <p>
     * Specify its generic type to be sure to get the best marshaller to manage it.
     * </p>
     *
     * @param genericType The generic type of the object. You can easily create parametrize type using
     *            {@link TypeUtils#parameterize(Class, Type...)}
     * @param object The object to convert as json.
     * @param ctx May be null - otherwise, use {@link CtxBuilder} to create the context.
     * @return the resulting json.
     * @since 7.2
     */
    public static <T> String objectToJson(Type genericType, T object, RenderingContext ctx) throws IOException {
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) object.getClass();
        Writer<T> writer = getService().getWriter(ctx, type, genericType, APPLICATION_JSON_TYPE);
        checkMarshaller(genericType, writer);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.write(object, type, genericType, APPLICATION_JSON_TYPE, baos);
        return baos.toString();
    }

    /**
     * Convert the given list to json.
     * <p>
     * Specify the list element type to get the best marshaller to manage conversion.
     * </p>
     *
     * @param elementType The element type of the list.
     * @param list The list to convert.
     * @param ctx May be null - otherwise, use {@link CtxBuilder} to create the context.
     * @return the resulting json.
     * @since 7.2
     */
    public static <T> String listToJson(Class<T> elementType, List<T> list, RenderingContext ctx) throws IOException {
        Type genericType = TypeUtils.parameterize(List.class, elementType);
        @SuppressWarnings("rawtypes")
        Writer<List> writer = getService().getWriter(ctx, List.class, genericType, APPLICATION_JSON_TYPE);
        checkMarshaller(genericType, writer);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.write(list, List.class, genericType, APPLICATION_JSON_TYPE, baos);
        return baos.toString();
    }

    /**
     * Read an object of the given type from given json.
     *
     * @param type The type of the read object.
     * @param json The json to parse.
     * @param ctx May be null - otherwise, use {@link CtxBuilder} to create the context.
     * @return the resulting object.
     * @since 7.2
     * @deprecated since 10.2 use {@link #jsonToObject(Class, String, RenderingContext)} instead.
     */
    @Deprecated
    public static <T> T objectToJson(Class<T> type, String json, RenderingContext ctx) throws IOException {
        return jsonToObject(type, json, ctx);
    }

    /**
     * @since 10.2
     */
    public static <T> T jsonToObject(Class<T> type, String json, RenderingContext ctx) throws IOException {
        Reader<T> reader = getService().getReader(ctx, type, APPLICATION_JSON_TYPE);
        checkMarshaller(type, reader);
        return reader.read(type, type, APPLICATION_JSON_TYPE, new ByteArrayInputStream(json.getBytes()));
    }

    /**
     * Read an object of the given type from given json.
     * <p>
     * Specify its generic type to be sure to get the best marshaller to manage it.
     * </p>
     *
     * @param type The type of the read object.
     * @param genericType The generic type of the object. You can easily create parametrize type using
     *            {@link TypeUtils#parameterize(Class, Type...)}
     * @param json The json to parse.
     * @param ctx May be null - otherwise, use {@link CtxBuilder} to create the context.
     * @return the resulting object.
     * @since 7.2
     * @deprecated since 10.2, use {@link #jsonToObject(Class, Type, String, RenderingContext)} instead.
     */
    @Deprecated
    public static <T> T objectToJson(Class<T> type, Type genericType, String json, RenderingContext ctx)
            throws IOException {
        return jsonToObject(type, genericType, json, ctx);
    }

    /**
     * @since 10.2
     */
    public static <T> T jsonToObject(Class<T> type, Type genericType, String json, RenderingContext ctx)
            throws IOException {
        Reader<T> reader = getService().getReader(ctx, type, genericType, APPLICATION_JSON_TYPE);
        checkMarshaller(genericType, reader);
        return reader.read(type, genericType, APPLICATION_JSON_TYPE, new ByteArrayInputStream(json.getBytes()));
    }

    /**
     * Read an object of the given type from given json.
     * <p>
     * Specify the list element type to get the best marshaller to manage conversion.
     * </p>
     *
     * @param elementType The element type of the list.
     * @param json The json to parse.
     * @param ctx May be null - otherwise, use {@link CtxBuilder} to create the context.
     * @return the resulting list.
     * @since 7.2
     * @deprecated since 10.2, use {@link #jsonToList(Class, String, RenderingContext)} instead.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static <T> List<T> listToJson(Class<T> elementType, String json, RenderingContext ctx) throws IOException {
        return jsonToList(elementType, json, ctx);
    }

    /**
     * @since 10.2
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> jsonToList(Class<T> elementType, String json, RenderingContext ctx) throws IOException {
        Type genericType = TypeUtils.parameterize(List.class, elementType);
        @SuppressWarnings("rawtypes")
        Reader<List> reader = getService().getReader(ctx, List.class, genericType, APPLICATION_JSON_TYPE);
        checkMarshaller(genericType, reader);
        return reader.read(List.class, genericType, APPLICATION_JSON_TYPE, new ByteArrayInputStream(json.getBytes()));
    }

}
