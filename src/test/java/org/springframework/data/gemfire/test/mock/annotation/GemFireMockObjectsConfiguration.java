/*
 * Copyright 2017-2020 the original author or authors.
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

package org.springframework.data.gemfire.test.mock.annotation;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.gemfire.test.mock.GemFireMockObjectsSupport;
import org.springframework.data.gemfire.test.mock.config.GemFireMockObjectsBeanPostProcessor;

/**
 * The {@link GemFireMockObjectsConfiguration} class is a Spring {@link Configuration @Configuration} class
 * containing bean definitions to configure Pivotal GemFireObject mocking.
 *
 * @author John Blum
 * @see java.lang.annotation.Annotation
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.ImportAware
 * @see org.springframework.core.annotation.AnnotationAttributes
 * @see org.springframework.core.type.AnnotationMetadata
 * @see GemFireMockObjectsBeanPostProcessor
 * @since 2.0.0
 */
@SuppressWarnings("unused")
@Configuration
public class GemFireMockObjectsConfiguration implements ApplicationListener<ContextClosedEvent>, ImportAware {

	private boolean useSingletonCache = false;

	@Override
	public void setImportMetadata(AnnotationMetadata importingClassMetadata) {

		if (isAnnotationPresent(importingClassMetadata)) {

			AnnotationAttributes enableGemFireMockingAttributes = getAnnotationAttributes(importingClassMetadata);

			this.useSingletonCache = enableGemFireMockingAttributes.getBoolean("useSingletonCache");
		}
	}

	private Class<? extends Annotation> getAnnotationType() {
		return EnableGemFireMockObjects.class;
	}

	private boolean isAnnotationPresent(AnnotationMetadata importingClassMetadata) {
		return isAnnotationPresent(importingClassMetadata, getAnnotationType());
	}

	private boolean isAnnotationPresent(AnnotationMetadata importingClassMetadata,
			Class<? extends Annotation> annotationType) {

		return importingClassMetadata.hasAnnotation(annotationType.getName());
	}

	private AnnotationAttributes getAnnotationAttributes(AnnotationMetadata importingClassMetadata) {
		return getAnnotationAttributes(importingClassMetadata, getAnnotationType());
	}

	private AnnotationAttributes getAnnotationAttributes(AnnotationMetadata importingClassMetadata,
			Class<? extends Annotation> annotationType) {

		return AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(annotationType.getName()));
	}

	@Bean
	public BeanPostProcessor mockGemFireObjectsBeanPostProcessor() {
		return GemFireMockObjectsBeanPostProcessor.newInstance(this.useSingletonCache);
	}

	@EventListener
	public void releaseMockObjectResources(ContextClosedEvent event) {
		GemFireMockObjectsSupport.destroy();
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		releaseMockObjectResources(event);
	}
}
