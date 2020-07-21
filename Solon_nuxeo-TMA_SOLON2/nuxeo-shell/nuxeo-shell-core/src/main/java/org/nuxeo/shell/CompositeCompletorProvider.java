/*
 * (C) Copyright 2006-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 */
package org.nuxeo.shell;

import java.util.ArrayList;
import java.util.List;

import jline.Completor;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class CompositeCompletorProvider implements CompletorProvider {

    protected List<CompletorProvider> providers = new ArrayList<CompletorProvider>();

    public void addProvider(CompletorProvider provider) {
        providers.add(provider);
    }

    public List<CompletorProvider> getProviders() {
        return providers;
    }

    public Completor getCompletor(Shell shell, CommandType cmd, Class<?> type) {
        for (CompletorProvider provider : providers) {
            Completor c = provider.getCompletor(shell, cmd, type);
            if (c != null) {
                return c;
            }
        }
        return null;
    }

}
