/*
 * (C) Copyright 2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 *
 * $Id: $
 */

package org.nuxeo.ecm.platform.ui.web.binding.alias;

import java.io.IOException;
import java.util.Map;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.MetaTagHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.jsf.ComponentSupport;

/**
 * Tag handler that exposes variables to the variable map. Behaviour is close
 * to the c:set tag handler except:
 * <ul>
 * <li>It handles several variables</li>
 * <li>It allows caching a variable using cache parameter: variable will be
 * resolved the first time is is called and will be put in the context after</li>
 * <li>The resolved variable is removed from context when tag is closed to
 * avoid filling the context with it</li>
 * <li>Variables are made available in the request context after the JSF
 * component tree build thanks to a backing component.</li>
 * </ul>
 * <p>
 * The backing component value expressions are changed even if the component
 * was found to ensure a good resolution even when re-rendering the tag using
 * ajax.
 *
 * @author <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 * @since 5.4
 */
public class AliasTagHandler extends MetaTagHandler {

    protected final TagAttribute cache;

    protected final TagAttribute id;

    protected final TagAttribute forceCreate;

    protected final Map<String, ValueExpression> variables;

    public AliasTagHandler(TagConfig config,
            Map<String, ValueExpression> variables) {
        super(config);
        id = getAttribute("id");
        cache = getAttribute("cache");
        forceCreate = getAttribute("forceCreate");
        this.variables = variables;
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        // make sure our parent is not null
        if (parent == null) {
            throw new TagException(this.tag, "Parent UIComponent was null");
        }

        // handle variable expression
        boolean cacheValue = false;
        if (cache != null) {
            cacheValue = cache.getBoolean(ctx);
        }
        boolean forceCreateValue = false;
        if (forceCreate != null) {
            forceCreateValue = forceCreate.getBoolean(ctx);
        }
        AliasVariableMapper target = new AliasVariableMapper();
        if (variables != null) {
            for (Map.Entry<String, ValueExpression> var : variables.entrySet()) {
                if (cacheValue) {
                    // resolve value and put it as is in variables
                    Object res = var.getValue().getValue(ctx);
                    target.setVariable(var.getKey(),
                            ctx.getExpressionFactory().createValueExpression(
                                    res, Object.class));
                } else {
                    target.setVariable(var.getKey(), var.getValue());
                }
            }
        }

        apply(ctx, parent, target, forceCreateValue);
    }

    public void apply(FaceletContext ctx, UIComponent parent,
            AliasVariableMapper alias, boolean forceCreate) throws IOException,
            FacesException, FaceletException, ELException {
        // our id
        String id = ctx.generateUniqueId(this.tagId);
        alias.setId(id);

        VariableMapper orig = ctx.getVariableMapper();
        VariableMapper vm = alias.getVariableMapperForBuild(orig);
        ctx.setVariableMapper(vm);

        // create component
        UIComponent c = ComponentSupport.findChildByTagId(parent, id);
        boolean componentFound = false;
        if (c != null && !forceCreate) {
            componentFound = true;
            // mark all children for cleaning
            ComponentSupport.markForDeletion(c);
        } else {
            c = new UIAliasHolder();

            // mark it owned by a facelet instance
            c.getAttributes().put(ComponentSupport.MARK_CREATED, id);

            // assign our unique id
            if (this.id != null) {
                c.setId(this.id.getValue(ctx));
            } else {
                UIViewRoot root = ComponentSupport.getViewRoot(ctx, parent);
                if (root != null) {
                    String uid = root.createUniqueId();
                    c.setId(uid);
                }
            }
        }

        // update value held
        c.setValueExpression("value",
                ctx.getExpressionFactory().createValueExpression(alias,
                        AliasVariableMapper.class));

        FacesContext facesContext = ctx.getFacesContext();
        try {
            AliasVariableMapper.exposeAliasesToRequest(facesContext, alias);
            // first allow c to get populated
            this.nextHandler.apply(ctx, c);
        } finally {
            AliasVariableMapper.removeAliasesExposedToRequest(facesContext, id);
            ctx.setVariableMapper(orig);
        }

        // finish cleaning up orphaned children
        if (componentFound) {
            ComponentSupport.finalizeForDeletion(c);
        }

        // add to the tree afterwards
        // this allows children to determine if it's
        // been part of the tree or not yet
        parent.getChildren().add(c);

    }

}
