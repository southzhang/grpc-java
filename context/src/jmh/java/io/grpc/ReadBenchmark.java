/*
 * Copyright 2017 The gRPC Authors
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

package io.grpc;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Read benchmark.
 */
public class ReadBenchmark {

    @State(Scope.Benchmark)
    public static class ContextState {
        List<Context.Key<Object>> keys = new ArrayList<>();
        List<Context> contexts = new ArrayList<>();

        @Setup
        public void setup() {
            for (int i = 0; i < 8; i++) {
                keys.add(Context.key("Key" + i));
            }
            contexts.add(Context.ROOT.withValue(keys.get(0), new Object()));
            contexts.add(Context.ROOT.withValues(keys.get(0), new Object(), keys.get(1), new Object()));
            contexts.add(
                    Context.ROOT.withValues(
                            keys.get(0), new Object(), keys.get(1), new Object(), keys.get(2), new Object()));
            contexts.add(
                    Context.ROOT.withValues(
                            keys.get(0),
                            new Object(),
                            keys.get(1),
                            new Object(),
                            keys.get(2),
                            new Object(),
                            keys.get(3),
                            new Object()));
            contexts.add(contexts.get(0).withValue(keys.get(1), new Object()));
            contexts.add(
                    contexts.get(1).withValues(keys.get(2), new Object(), keys.get(3), new Object()));
            contexts.add(
                    contexts
                            .get(2)
                            .withValues(
                                    keys.get(3), new Object(), keys.get(4), new Object(), keys.get(5), new Object()));
            contexts.add(
                    contexts
                            .get(3)
                            .withValues(
                                    keys.get(4),
                                    new Object(),
                                    keys.get(5),
                                    new Object(),
                                    keys.get(6),
                                    new Object(),
                                    keys.get(7),
                                    new Object()));
        }
    }

    /**
     * Perform the read operation.
     */
    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testContextLookup(ContextState state, Blackhole bh) {
        for (Context.Key<?> key : state.keys) {
            for (Context ctx : state.contexts) {
                bh.consume(key.get(ctx));
            }
        }
    }
}
