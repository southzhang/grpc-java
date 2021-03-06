/*
 * Copyright 2015 The gRPC Authors
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
 */

package io.grpc.protobuf;

import com.google.common.io.ByteStreams;
import com.google.protobuf.Type;
import io.grpc.MethodDescriptor.Marshaller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ProtoUtils}.
 */
@RunWith(JUnit4.class)
public class ProtoUtilsTest {
    private Type proto = Type.newBuilder().setName("value").build();

    @Test
    public void testRoundtrip() throws Exception {
        Marshaller<Type> marshaller = ProtoUtils.marshaller(Type.getDefaultInstance());
        InputStream is = marshaller.stream(proto);
        is = new ByteArrayInputStream(ByteStreams.toByteArray(is));
        assertEquals(proto, marshaller.parse(is));
    }

    @Test
    public void keyForProto() {
        assertEquals("google.protobuf.Type-bin",
                ProtoUtils.keyForProto(Type.getDefaultInstance()).originalName());
    }
}
