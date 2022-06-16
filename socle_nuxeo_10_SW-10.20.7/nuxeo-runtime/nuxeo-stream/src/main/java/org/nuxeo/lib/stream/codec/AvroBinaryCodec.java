/*
 * (C) Copyright 2018 Nuxeo SA (http://nuxeo.com/) and others.
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
package org.nuxeo.lib.stream.codec;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.message.RawMessageDecoder;
import org.apache.avro.message.RawMessageEncoder;
import org.apache.avro.reflect.ReflectData;

/**
 * Avro Binary format, there is no header, the schema must be the same for encoder and decoder.
 *
 * @since 10.2
 */
public class AvroBinaryCodec<T> implements Codec<T> {
    public static final String NAME = "avroBinary";

    protected final Class<T> messageClass;

    protected final Schema schema;

    protected final RawMessageEncoder<T> encoder;

    protected final RawMessageDecoder<T> decoder;

    public AvroBinaryCodec(Class<T> messageClass) {
        this.messageClass = messageClass;
        schema = ReflectData.get().getSchema(messageClass);
        encoder = new RawMessageEncoder<>(ReflectData.get(), schema);
        decoder = new RawMessageDecoder<>(ReflectData.get(), schema);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] encode(T object) {
        try {
            return encoder.encode(object).array();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public T decode(byte[] data) {
        try {
            return decoder.decode(data, null);
        } catch (IOException | IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
