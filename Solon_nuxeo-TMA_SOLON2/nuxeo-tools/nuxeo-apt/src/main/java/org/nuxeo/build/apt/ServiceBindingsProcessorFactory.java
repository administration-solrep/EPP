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
 *     bstefanescu
 *
 * $Id$
 */

package org.nuxeo.build.apt;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableCollection;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.apt.Filer;
import com.sun.mirror.apt.Messager;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.util.DeclarationFilter;

/**
 *
 * Process all Stateless and Stateful service bean annotations and generate a service binding
 * by getting the Remote annotation. Service bindings are put in a OSGI-INF/service.bindings file
 * in the java properties format.
 * <p>
 * Each binding will map the service remote interface to the service bean implementation.
 * Example: <code>org.nuxeo.MyService=org.nuxeo.ejb.MyServiceBean</code>
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ServiceBindingsProcessorFactory implements AnnotationProcessorFactory {

    // Process any set of annotations
    private static final Collection<String> supportedAnnotations = unmodifiableCollection(Arrays.asList("javax.ejb.Stateless", "javax.ejb.Stateful", "javax.ejb.Remote"));

    // No supported options
    private static final Collection<String> supportedOptions = emptySet();

    public Collection<String> supportedAnnotationTypes() {
        return supportedAnnotations;
    }

    public Collection<String> supportedOptions() {
        return supportedOptions;
    }

    public AnnotationProcessor getProcessorFor(
            Set<AnnotationTypeDeclaration> atds,
            AnnotationProcessorEnvironment env) {
        AnnotationProcessor result = null;
        if (atds.isEmpty()) {
            result = AnnotationProcessors.NO_OP;
        } else {
            result = new ServiceBindingsProcessor(env);
        }
        return result;
    }

    private static class ServiceBindingsProcessor implements AnnotationProcessor {
        private final AnnotationProcessorEnvironment env;
        AnnotationTypeDeclaration statelessAnno;
        AnnotationTypeDeclaration statefulAnno;
        AnnotationTypeDeclaration remoteAnno;

        ServiceBindingsProcessor(AnnotationProcessorEnvironment env) {
            this.env = env;
            remoteAnno = (AnnotationTypeDeclaration) env.getTypeDeclaration("javax.ejb.Remote");
            statelessAnno = (AnnotationTypeDeclaration) env.getTypeDeclaration("javax.ejb.Stateless");
            statefulAnno = (AnnotationTypeDeclaration) env.getTypeDeclaration("javax.ejb.Stateful");
        }

        public void process() {
            try {
                Messager log = env.getMessager();
                PrintWriter writer = env.getFiler().createTextFile(Filer.Location.CLASS_TREE, "",
                        new File("OSGI-INF/service.bindings"), "UTF-8");
                writer.println("# This is a generated file an contains service bindigns in the form: ");
                writer.println("# SERVICE_API = SERVICE_BEAN_CLASS");

                // Get all declarations that use the note annotation.
                Collection<Declaration> declarations = env.getDeclarationsAnnotatedWith(statefulAnno);
                declarations.addAll(env.getDeclarationsAnnotatedWith(statelessAnno));

                DeclarationFilter filter = DeclarationFilter.getFilter(ClassDeclaration.class);
                declarations = filter.filter(declarations);

                for (Declaration declaration : declarations) {
                    //log.printNotice("Introspecting class: "+declaration.getSimpleName());
                    Collection<AnnotationMirror> annotations = declaration.getAnnotationMirrors();
                    for (AnnotationMirror anno : annotations) {
                        //log.printNotice("Found Annotation: "+anno.getAnnotationType().getDeclaration().getSimpleName());
                        if (anno.getAnnotationType().getDeclaration().equals(remoteAnno)) {
                            Map<AnnotationTypeElementDeclaration, AnnotationValue> map = anno.getElementValues();
                            ArrayList<?> remoteItf = null;
                            for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry : map.entrySet()) {
                                if ("value".equals(entry.getKey().getSimpleName())) {
                                    remoteItf = (ArrayList<?>)entry.getValue().getValue();
                                    break;
                                }
                            }
                            if (remoteItf != null) {
                                String value = ((ClassDeclaration)declaration).getQualifiedName();
                                for (Object itf : remoteItf) {
                                    AnnotationValue av = (AnnotationValue)itf;
                                    String key = ((InterfaceType)av.getValue()).getDeclaration().getQualifiedName();
                                    //log.printNotice("Found Service Binding: "+key+" => "+value);
                                    writer.println(key+"="+value);
                                    // write also any super interface that is not part of java runtime.
                                    Collection<InterfaceType> superItfs = ((InterfaceType)av.getValue()).getSuperinterfaces();
                                    for (InterfaceType superItf : superItfs) {
                                        key = superItf.getDeclaration().getQualifiedName();
                                        if (!key.startsWith("java")) {
                                            writer.println(key+"="+value);
                                        }
                                    }
                                }
                            } else {
                                log.printWarning("javax.ejb.Remote annotation without parameters on class "+declaration.getSimpleName());
                            }
                        }
                    }
                }

                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
