/*
 * (C) Copyright 2017 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     bdelbosc
 */
package org.nuxeo.lib.stream.computation.log;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.lib.stream.codec.Codec;
import org.nuxeo.lib.stream.computation.Record;
import org.nuxeo.lib.stream.computation.RecordFilter;
import org.nuxeo.lib.stream.computation.RecordFilterChain;
import org.nuxeo.lib.stream.computation.Settings;
import org.nuxeo.lib.stream.computation.StreamManager;
import org.nuxeo.lib.stream.computation.StreamProcessor;
import org.nuxeo.lib.stream.computation.Topology;
import org.nuxeo.lib.stream.log.LogManager;
import org.nuxeo.lib.stream.log.LogOffset;
import org.nuxeo.lib.stream.log.LogPartition;
import org.nuxeo.lib.stream.log.LogTailer;
import org.nuxeo.lib.stream.log.RebalanceListener;
import org.nuxeo.lib.stream.log.internals.LogOffsetImpl;

/**
 * StreamManager based on a LogManager
 *
 * @since 11.1
 */
public class LogStreamManager implements StreamManager {
    private static final Log log = LogFactory.getLog(LogStreamManager.class);

    protected final LogManager logManager;

    public LogStreamManager(LogManager logManager) {
        this.logManager = logManager;
    }

    protected final Map<String, Topology> topologies = new HashMap<>();

    protected final Map<String, Settings> settings = new HashMap<>();

    protected final Map<String, RecordFilterChain> filters = new HashMap<>();

    protected final Set<String> streams = new HashSet<>();

    @Override
    public void register(String processorName, Topology topology, Settings settings) {
        log.debug("Register processor: " + processorName);
        topologies.put(processorName, topology);
        this.settings.put(processorName, settings);
        initStreams(topology, settings);
        initAppenders(topology, settings);
        registerFilters(topology, settings);
    }

    @Override
    public StreamProcessor createStreamProcessor(String processorName) {
        if (!topologies.containsKey(processorName)) {
            throw new IllegalArgumentException("Unregistered processor name: " + processorName);
        }
        LogStreamProcessor processor = new LogStreamProcessor(this);
        processor.init(topologies.get(processorName), settings.get(processorName));
        return processor;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    @Override
    public LogOffset append(String stream, Record record) {
        RecordFilterChain filter = filters.get(stream);
        if (filter == null) {
            throw new IllegalArgumentException("Unknown stream: " + stream);
        }
        record = filter.beforeAppend(record);
        if (record == null) {
            return new LogOffsetImpl(stream, 0, 0);
        }
        LogOffset offset = logManager.getAppender(stream).append(record.getKey(), record);
        filter.afterAppend(record, offset);
        return offset;
    }

    public boolean supportSubscribe() {
        return logManager.supportSubscribe();
    }

    public LogTailer<Record> subscribe(String computationName, Collection<String> streams, RebalanceListener listener) {
        Codec<Record> codec = getCodec(streams);
        return logManager.subscribe(computationName, streams, listener, codec);
    }

    public LogTailer<Record> createTailer(String computationName, Collection<LogPartition> streamPartitions) {
        if (streamPartitions.isEmpty()) {
            return logManager.createTailer(computationName, streamPartitions);
        }
        Codec<Record> codec = getCodec(streamPartitions.stream().map(LogPartition::name).collect(Collectors.toList()));
        return logManager.createTailer(computationName, streamPartitions, codec);
    }

    public RecordFilter getFilter(String stream) {
        return filters.get(stream);
    }

    protected Codec<Record> getCodec(Collection<String> streams) {
        Codec<Record> codec = null;
        for (String stream : streams) {
            Codec<Record> sCodec = logManager.<Record> getAppender(stream).getCodec();
            if (codec == null) {
                codec = sCodec;
            } else if (!codec.getName().equals(sCodec.getName())) {
                throw new IllegalArgumentException("Different codec on input streams are not supported " + streams);
            }
        }
        return codec;
    }

    protected void initStreams(Topology topology, Settings settings) {
        log.debug("Initializing streams");
        topology.streamsSet().forEach(streamName -> {
            if (logManager.exists(streamName)) {
                int size = logManager.size(streamName);
                if (settings.getPartitions(streamName) != size) {
                    log.debug(String.format(
                            "Update settings for stream: %s defined with %d partitions but exists with %d partitions",
                            streamName, settings.getPartitions(streamName), size));
                    settings.setPartitions(streamName, size);
                }
            } else {
                logManager.createIfNotExists(streamName, settings.getPartitions(streamName));
            }
            streams.add(streamName);
        });
    }

    protected void initAppenders(Topology topology, Settings settings) {
        log.debug("Initializing source appenders so we ensure they use codec defined in the processor");
        topology.streamsSet()
                .forEach(stream -> logManager.getAppender(stream, settings.getCodec(stream)));
    }

    protected void registerFilters(Topology topology, Settings settings) {
        topology.streamsSet().forEach(stream -> filters.put(stream, settings.getFilterChain(stream)));
    }

}
