/*
 * Copyright 2018 The gRPC Authors
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

package io.grpc.util;

import io.grpc.ForwardingTestUtil;
import io.grpc.LoadBalancer.Subchannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link ForwardingSubchannel}.
 */
@RunWith(JUnit4.class)
public class ForwardingSubchannelTest {
    private final Subchannel mockDelegate = mock(Subchannel.class);

    private final class TestSubchannel extends ForwardingSubchannel {
        @Override
        protected Subchannel delegate() {
            return mockDelegate;
        }
    }

    @Test
    public void allMethodsForwarded() throws Exception {
        ForwardingTestUtil.testMethodsForwarded(
                Subchannel.class,
                mockDelegate,
                new TestSubchannel(),
                Arrays.asList(Subchannel.class.getMethod("getAddresses")));
    }
}
