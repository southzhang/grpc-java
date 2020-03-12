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

package io.grpc.internal;

import io.grpc.*;
import org.openjdk.jmh.annotations.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark for {@link StatsTraceContext}.
 */
@State(Scope.Benchmark)
public class StatsTraceContextBenchmark {

    private final String methodName = MethodDescriptor.generateFullMethodName("service", "method");

    private final Metadata emptyMetadata = new Metadata();
    private final List<ServerStreamTracer.Factory> serverStreamTracerFactories =
            Collections.emptyList();

    /**
     * Javadoc comment.
     */
    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public StatsTraceContext newClientContext() {
        return StatsTraceContext.newClientContext(CallOptions.DEFAULT, Attributes.EMPTY, emptyMetadata);
    }

    /**
     * Javadoc comment.
     */
    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public StatsTraceContext newServerContext_empty() {
        return StatsTraceContext.newServerContext(
                serverStreamTracerFactories, methodName, emptyMetadata);
    }
}
