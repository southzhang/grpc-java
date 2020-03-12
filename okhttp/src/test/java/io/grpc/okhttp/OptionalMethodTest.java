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

package io.grpc.okhttp;

import io.grpc.okhttp.internal.OptionalMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * Tests for OptionalMethod.
 */
@RunWith(JUnit4.class)
public class OptionalMethodTest {

    public static class DefaultClass {
        public String testMethod(String arg) {
            return arg;
        }
    }

    public abstract static class PublicParent {
        public abstract String testMethod(String arg);
    }

    private static class PrivateImpl extends PublicParent {
        @Override
        public String testMethod(String arg) {
            return arg;
        }
    }

    private static class PrivateClass {
        public String testMethod(String arg) {
            return arg;
        }
    }

    @Test
    public void isSupported() {
        OptionalMethod<DefaultClass> defaultClassMethod = new OptionalMethod<>(
                String.class, "testMethod", String.class);
        assertTrue(defaultClassMethod.isSupported(new DefaultClass()));

        OptionalMethod<PublicParent> privateImpl = new OptionalMethod<>(
                String.class, "testMethod", String.class);
        assertTrue(privateImpl.isSupported(new PrivateImpl()));

        OptionalMethod<PrivateClass> privateClass = new OptionalMethod<>(
                String.class, "testMethod", String.class);
        assertFalse(privateClass.isSupported(new PrivateClass()));
    }

    @Test
    public void invokeOptional() throws InvocationTargetException {
        OptionalMethod<DefaultClass> defaultClassMethod = new OptionalMethod<>(
                String.class, "testMethod", String.class);
        assertEquals("testArg", defaultClassMethod.invokeOptional(new DefaultClass(), "testArg"));

        OptionalMethod<PublicParent> privateImpl = new OptionalMethod<>(
                String.class, "testMethod", String.class);
        assertEquals("testArg", privateImpl.invokeOptional(new PrivateImpl(), "testArg"));

        OptionalMethod<PrivateClass> privateClass = new OptionalMethod<>(
                String.class, "testMethod", String.class);
        assertEquals(null, privateClass.invokeOptional(new PrivateClass(), "testArg"));
    }
}
