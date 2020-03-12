/*
 * Copyright 2020 The gRPC Authors
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

package io.grpc.xds;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.grpc.LoadBalancer;
import io.grpc.LoadBalancer.Helper;
import io.grpc.LoadBalancerProvider;
import io.grpc.LoadBalancerRegistry;
import io.grpc.NameResolver.ConfigOrError;
import io.grpc.internal.JsonParser;
import io.grpc.internal.ServiceConfigUtil.PolicySelection;
import io.grpc.xds.XdsRoutingLoadBalancerProvider.MethodName;
import io.grpc.xds.XdsRoutingLoadBalancerProvider.Route;
import io.grpc.xds.XdsRoutingLoadBalancerProvider.XdsRoutingConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link XdsRoutingLoadBalancerProvider}.
 */
@RunWith(JUnit4.class)
public class XdsRoutingLoadBalancerProviderTest {

    @Test
    public void parseWeightedTargetConfig() throws Exception {
        LoadBalancerRegistry lbRegistry = new LoadBalancerRegistry();
        XdsRoutingLoadBalancerProvider xdsRoutingLoadBalancerProvider =
                new XdsRoutingLoadBalancerProvider(lbRegistry);
        final Object fooConfig = new Object();
        LoadBalancerProvider lbProviderFoo = new LoadBalancerProvider() {
            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public int getPriority() {
                return 5;
            }

            @Override
            public String getPolicyName() {
                return "foo_policy";
            }

            @Override
            public LoadBalancer newLoadBalancer(Helper helper) {
                return mock(LoadBalancer.class);
            }

            @Override
            public ConfigOrError parseLoadBalancingPolicyConfig(Map<String, ?> rawConfig) {
                return ConfigOrError.fromConfig(fooConfig);
            }
        };
        final Object barConfig = new Object();
        LoadBalancerProvider lbProviderBar = new LoadBalancerProvider() {
            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public int getPriority() {
                return 5;
            }

            @Override
            public String getPolicyName() {
                return "bar_policy";
            }

            @Override
            public LoadBalancer newLoadBalancer(Helper helper) {
                return mock(LoadBalancer.class);
            }

            @Override
            public ConfigOrError parseLoadBalancingPolicyConfig(Map<String, ?> rawConfig) {
                return ConfigOrError.fromConfig(barConfig);
            }
        };
        lbRegistry.register(lbProviderFoo);
        lbRegistry.register(lbProviderBar);

        String xdsRoutingConfigJson = ("{"
                + "  'route' : ["
                + "    {"
                + "      'methodName' : {'service' : 'service_foo', 'method' : 'method_foo'},"
                + "      'action' : 'action_foo'"
                + "    },"
                + "    {"
                + "      'methodName' : {'service' : '', 'method' : ''},"
                + "      'action' : 'action_bar'"
                + "    }"
                + "  ],"
                + "  'action' : {"
                + "    'action_foo' : {"
                + "      'childPolicy' : ["
                + "        {'unsupported_policy' : {}},"
                + "        {'foo_policy' : {}}"
                + "      ]"
                + "    },"
                + "    'action_bar' : {"
                + "      'childPolicy' : ["
                + "        {'unsupported_policy' : {}},"
                + "        {'bar_policy' : {}}"
                + "      ]"
                + "    }"
                + "  }"
                + "}").replace("'", "\"");

        @SuppressWarnings("unchecked")
        Map<String, ?> rawLbConfigMap = (Map<String, ?>) JsonParser.parse(xdsRoutingConfigJson);
        ConfigOrError configOrError =
                xdsRoutingLoadBalancerProvider.parseLoadBalancingPolicyConfig(rawLbConfigMap);
        assertThat(configOrError).isEqualTo(
                ConfigOrError.fromConfig(
                        new XdsRoutingConfig(
                                ImmutableList.of(
                                        new Route("action_foo", new MethodName("service_foo", "method_foo")),
                                        new Route("action_bar", new MethodName("", ""))),
                                ImmutableMap.of(
                                        "action_foo",
                                        new PolicySelection(lbProviderFoo, new HashMap<String, Object>(), fooConfig),
                                        "action_bar",
                                        new PolicySelection(
                                                lbProviderBar, new HashMap<String, Object>(), barConfig)))));
    }
}