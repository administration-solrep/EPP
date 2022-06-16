/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
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
 *     Gethin James
 */
package org.nuxeo.elasticsearch.aggregate;

import java.util.Collections;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.SingleBucketAggregation;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.query.api.AggregateDefinition;
import org.nuxeo.ecm.platform.query.core.BucketTerm;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An aggregate that returns a single bucket.
 *
 * @since 10.3
 */
public abstract class SingleBucketAggregate extends AggregateEsBase<SingleBucketAggregation, BucketTerm> {

    protected long docCount;

    public SingleBucketAggregate(AggregateDefinition definition, DocumentModel searchDocument) {
        super(definition, searchDocument);
    }

    @Override
    public void parseAggregation(SingleBucketAggregation aggregation) {
        this.docCount = aggregation.getDocCount();
        this.buckets = Collections.singletonList(new BucketTerm(definition.getType(), docCount));
    }

    @JsonIgnore
    @Override
    public QueryBuilder getEsFilter() {
        if (getSelection().isEmpty()) {
            return null;
        }
        return QueryBuilders.termsQuery(getField(), getSelection());
    }

    public long getDocCount() {
        return docCount;
    }
}
