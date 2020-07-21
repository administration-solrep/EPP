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
 *     bchaffangeon
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.syndication.serializer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.tag.fn.DocumentModelFunctions;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Response;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * @author bchaffangeon
 *
 */
public class ATOMSerializer extends AbstractSyndicationSerializer 
         {

    private static final DateFormat DATE_PARSER = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    private static final String ATOM_TYPE_old = "atom_0.3";
    private static final String ATOM_TYPE = "atom_1.0";

    @Override
    public String serialize(ResultSummary summary, DocumentModelList docList,
            List<String> columnsDefinition, HttpServletRequest req) {
        setSyndicationFormat(ATOM_TYPE);
        return super.serialize(summary, docList, columnsDefinition, req);
    }



}
