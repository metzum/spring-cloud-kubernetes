/*
 * Copyright 2013-2019 the original author or authors.
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * Config map configuration properties.
 *
 * @author Ioannis Canellos
 */
@ConfigurationProperties("spring.cloud.kubernetes.config")
public class ConfigMapConfigProperties extends AbstractConfigProperties {

	private static final String TARGET = "Config Map";

	private boolean enableApi = true;

	private List<String> paths = new LinkedList<>();

	private List<Source> sources = new LinkedList<>();

	public boolean isEnableApi() {
		return this.enableApi;
	}

	public void setEnableApi(boolean enableApi) {
		this.enableApi = enableApi;
	}

	public List<String> getPaths() {
		return this.paths;
	}

	public void setPaths(List<String> paths) {
		this.paths = paths;
	}

	public List<Source> getSources() {
		return this.sources;
	}

	public void setSources(List<Source> sources) {
		this.sources = sources;
	}

	/**
	 * @return A list of Source to use If the user has not specified any Source
	 * properties, then a single Source is constructed based on the supplied name and
	 * namespace
	 *
	 * These are the actual name/namespace pairs that are used to create a
	 * ConfigMapPropertySource
	 */
	public List<NormalizedSource> determineSources() {
		if (this.sources.isEmpty()) {
			return new ArrayList<NormalizedSource>() {
				{
					add(new NormalizedSource(ConfigMapConfigProperties.this.name,
							ConfigMapConfigProperties.this.namespace, null, null));
				}
			};
		}

		return this.sources.stream().map(s -> s.normalize(this.name, this.namespace))
				.collect(Collectors.toList());
	}

	@Override
	public String getConfigurationTarget() {
		return TARGET;
	}

	/**
	 * Config map source.
	 */
	public static class Source {

		/**
		 * The name of the ConfigMap.
		 */
		private String name;

		/**
		 * The namespace where the ConfigMap is found.
		 */
		private String namespace;

		/**
		 * A label name, optionally with a label value) that may match one or more
		 * ConfigMap.
		 */
		private String label;

		public Source() {
		}

		public Source(String name, String namespace) {
			this(name, namespace, null);
			this.name = name;
		}

		public Source(String name, String namespace, String label) {
			this.name = name;
			this.namespace = namespace;
			this.label = label;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getNamespace() {
			return this.namespace;
		}

		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public boolean isEmpty() {
			return StringUtils.isEmpty(this.name) && StringUtils.isEmpty(this.namespace)
					&& StringUtils.isEmpty(this.label);
		}

		public NormalizedSource normalize(String defaultName, String defaultNamespace) {
			final String normalizedName;
			final String normalizedLabelName;
			final String normalizedLabelValue;
			if (StringUtils.isEmpty(label)) {
				normalizedName = StringUtils.isEmpty(this.name) ? defaultName : this.name;
				normalizedLabelName = null;
				normalizedLabelValue = null;
			}
			else {
				String[] parts = label.split("=");
				normalizedName = this.name;
				normalizedLabelName = parts[0];
				normalizedLabelValue = parts.length > 1 ? parts[1] : null;
			}
			final String normalizedNamespace = StringUtils.isEmpty(this.namespace)
					? defaultNamespace : this.namespace;

			return new NormalizedSource(normalizedName, normalizedNamespace,
					normalizedLabelName, normalizedLabelValue);
		}

	}

	static class NormalizedSource {

		private final String name;

		private final String namespace;

		private final String labelName;

		private final String labelValue;

		NormalizedSource(String name, String namespace, String labelName,
				String labelValue) {
			this.name = name;
			this.namespace = namespace;
			this.labelName = labelName;
			this.labelValue = labelValue;
		}

		public String getName() {
			return this.name;
		}

		public String getNamespace() {
			return this.namespace;
		}

		public String getLabelName() {
			return labelName;
		}

		public String getLabelValue() {
			return labelValue;
		}

	}

}
