/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.kubernetes.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Kjetil Iversen
 */
public class ConfigMapConfigPropertiesTest {

	ConfigMapConfigProperties props;

	String defaultName = "defaultNameValue";

	String defaultNamespace = "defaultNamespaceValue";

	@BeforeEach
	public void setup() {
		props = new ConfigMapConfigProperties();
		props.setName(defaultName);
		props.setNamespace(defaultNamespace);
	}

	@Test
	public void testDefaultNameAndNamespace() {
		props.setSources(Collections.emptyList());
		List<ConfigMapConfigProperties.NormalizedSource> normalizedSources = props.determineSources();
		assertThat(normalizedSources).hasSize(1).first().hasFieldOrPropertyWithValue("name", defaultName)
				.hasFieldOrPropertyWithValue("namespace", defaultNamespace);
	}

	@Test
	public void testNoLabels() {
		props.setSources(
				Collections.singletonList(new ConfigMapConfigProperties.Source("nameValue", "namespaceValue")));
		List<ConfigMapConfigProperties.NormalizedSource> normalizedSources = props.determineSources();
		assertThat(normalizedSources).hasSize(1).first().hasFieldOrPropertyWithValue("name", "nameValue")
				.hasFieldOrPropertyWithValue("namespace", "namespaceValue");
	}

	@Test
	public void testLabelName() {
		props.setSources(Arrays.asList(new ConfigMapConfigProperties.Source(null, null, "labelNameValue1"),
				new ConfigMapConfigProperties.Source("nameValue", null, "labelNameValue2"),
				new ConfigMapConfigProperties.Source(null, "namespaceValue", "labelNameValue3")));
		List<ConfigMapConfigProperties.NormalizedSource> normalizedSources = props.determineSources();
		assertThat(normalizedSources).hasSize(3);
		assertThat(normalizedSources).element(0).hasFieldOrPropertyWithValue("name", null)
				.hasFieldOrPropertyWithValue("namespace", defaultNamespace)
				.hasFieldOrPropertyWithValue("labelName", "labelNameValue1")
				.hasFieldOrPropertyWithValue("labelValue", null);
		assertThat(normalizedSources).element(1).hasFieldOrPropertyWithValue("name", "nameValue")
				.hasFieldOrPropertyWithValue("namespace", defaultNamespace)
				.hasFieldOrPropertyWithValue("labelName", "labelNameValue2")
				.hasFieldOrPropertyWithValue("labelValue", null);
		assertThat(normalizedSources).element(2).hasFieldOrPropertyWithValue("name", null)
				.hasFieldOrPropertyWithValue("namespace", "namespaceValue")
				.hasFieldOrPropertyWithValue("labelName", "labelNameValue3")
				.hasFieldOrPropertyWithValue("labelValue", null);
	}

	@Test
	public void testLabelValue() {
		props.setSources(Collections
				.singletonList(new ConfigMapConfigProperties.Source(null, null, "labelNameValue=labelValueValue")));
		List<ConfigMapConfigProperties.NormalizedSource> normalizedSources = props.determineSources();
		assertThat(normalizedSources).hasSize(1).first().hasFieldOrPropertyWithValue("name", null)
				.hasFieldOrPropertyWithValue("namespace", defaultNamespace)
				.hasFieldOrPropertyWithValue("labelName", "labelNameValue")
				.hasFieldOrPropertyWithValue("labelValue", "labelValueValue");
	}

}
