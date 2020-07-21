/*
 * (C) Copyright 2002-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 */
package org.nuxeo.ecm.core.convert.plugins.text.extractors;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.convert.api.ConversionException;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.core.convert.extension.Converter;
import org.nuxeo.ecm.core.convert.extension.ConverterDescriptor;
import org.nuxeo.runtime.api.Framework;

/**
 * Converter that tries to find a way to extract full text content according to input mime-type.
 *
 * @author tiry
 */
public class FullTextConverter implements Converter {

    private static final String TEXT_PLAIN_MT = "text/plain";
    private static final Log log = LogFactory.getLog(FullTextConverter.class);

    protected ConverterDescriptor descriptor;

    @Override
    public BlobHolder convert(BlobHolder blobHolder,
            Map<String, Serializable> parameters) throws ConversionException {

        String srcMT;
        try {
            srcMT = blobHolder.getBlob().getMimeType();
        } catch (ClientException e) {
            throw new ConversionException("Unable to get source MimeType", e);
        }

        if (TEXT_PLAIN_MT.equals(srcMT)) {
            // no need to convert !
            return blobHolder;
        }

        ConversionService cs = Framework.getLocalService(ConversionService.class);

        String converterName = cs.getConverterName(srcMT, TEXT_PLAIN_MT);

        if (converterName != null) {
            if (converterName.equals(descriptor.getConverterName())) {
                // Should never happen !
                log.debug("Existing from converter to avoid a loop");
                return new SimpleBlobHolder(new StringBlob(""));
            }
            return cs.convert(converterName, blobHolder, parameters);
        } else {
            log.debug("Unable to find full text extractor for source mime type" + srcMT);
            return new SimpleBlobHolder(new StringBlob(""));
        }
    }

    @Override
    public void init(ConverterDescriptor descriptor) {
        this.descriptor = descriptor;
    }

}
