/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id: TagConfigFactory.java 28460 2008-01-03 15:34:05Z sfermigier $
 */

package org.nuxeo.ecm.platform.forms.layout.facelets;

import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttributes;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ConverterConfig;
import com.sun.facelets.tag.jsf.ValidatorConfig;

/**
 * Helper for generating configs outside of a library context.
 *
 * @author <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 */
public final class TagConfigFactory {

    private TagConfigFactory() {
    }

    private static class TagConfigWrapper implements TagConfig {

        protected final Tag tag;

        protected final String tagId;

        protected final FaceletHandler nextHandler;

        TagConfigWrapper(TagConfig tagConfig, TagAttributes attributes,
                FaceletHandler nextHandler) {
            tag = new Tag(tagConfig.getTag(), attributes);
            tagId = tagConfig.getTagId();
            this.nextHandler = nextHandler;
        }

        public FaceletHandler getNextHandler() {
            return nextHandler;
        }

        public Tag getTag() {
            return tag;
        }

        public String getTagId() {
            return tagId;
        }
    }

    private static class ComponentConfigWrapper extends TagConfigWrapper
            implements ComponentConfig {

        protected final String componentType;

        protected final String rendererType;

        ComponentConfigWrapper(TagConfig tagConfig,
                TagAttributes attributes, FaceletHandler nextHandler,
                String componentType, String rendererType) {
            super(tagConfig, attributes, nextHandler);
            this.componentType = componentType;
            this.rendererType = rendererType;
        }

        public String getComponentType() {
            return componentType;
        }

        public String getRendererType() {
            return rendererType;
        }
    }

    private static class ConverterConfigWrapper extends TagConfigWrapper
            implements ConverterConfig {

        protected final String converterId;

        ConverterConfigWrapper(TagConfig tagConfig,
                TagAttributes attributes, FaceletHandler nextHandler,
                String converterId) {
            super(tagConfig, attributes, nextHandler);
            this.converterId = converterId;
        }

        public String getConverterId() {
            return converterId;
        }
    }

    private static class ValidatorConfigWrapper extends TagConfigWrapper
            implements ValidatorConfig {

        protected final String validatorId;

        ValidatorConfigWrapper(TagConfig tagConfig,
                TagAttributes attributes, FaceletHandler nextHandler,
                String validatorId) {
            super(tagConfig, attributes, nextHandler);
            this.validatorId = validatorId;
        }

        public String getValidatorId() {
            return validatorId;
        }
    }

    public static TagConfig createTagConfig(TagConfig tagConfig,
            TagAttributes attributes, FaceletHandler nextHandler) {
        return new TagConfigWrapper(tagConfig, attributes, nextHandler);
    }

    public static ComponentConfig createComponentConfig(TagConfig tagConfig,
            TagAttributes attributes, FaceletHandler nextHandler,
            String componentType, String rendererType) {
        return new ComponentConfigWrapper(tagConfig, attributes, nextHandler,
                componentType, rendererType);
    }

    public static ConverterConfig createConverterConfig(TagConfig tagConfig,
            TagAttributes attributes, FaceletHandler nextHandler,
            String converterId) {
        return new ConverterConfigWrapper(tagConfig, attributes, nextHandler,
                converterId);
    }

    public static ValidatorConfig createValidatorConfig(TagConfig tagConfig,
            TagAttributes attributes, FaceletHandler nextHandler,
            String validatorId) {
        return new ValidatorConfigWrapper(tagConfig, attributes, nextHandler,
                validatorId);
    }

}
