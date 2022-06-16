/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.
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
 *      Vladimir Pasquier <vpasquier@nuxeo.com>
 */
package org.nuxeo.binary.metadata.internals;

import org.nuxeo.binary.metadata.api.BinaryMetadataProcessor;
import org.nuxeo.runtime.model.SimpleContributionRegistry;

/**
 * Registry for {@link org.nuxeo.binary.metadata.internals.MetadataProcessorDescriptor} descriptors.
 *
 * @since 7.1
 */
public class MetadataProcessorRegistry extends SimpleContributionRegistry<MetadataProcessorDescriptor> {

    @Override
    public String getContributionId(MetadataProcessorDescriptor metadataProcessorDescriptor) {
        return metadataProcessorDescriptor.getId();
    }

    /**
     * Not supported.
     */
    @Override
    public void merge(MetadataProcessorDescriptor metadataProcessorDescriptor,
            MetadataProcessorDescriptor metadataProcessorDescriptor2) {
        throw new UnsupportedOperationException();
    }

    public BinaryMetadataProcessor getProcessor(String processorId) {
        return currentContribs.get(processorId).processor;
    }
}
