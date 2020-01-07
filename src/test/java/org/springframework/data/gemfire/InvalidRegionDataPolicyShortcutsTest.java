/*
 * Copyright 2010-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Integration Tests testing and setting up some invalid, or illegal uses of the Region data-policy and shortcut
 * XML namespace attributes.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.context.support.ClassPathXmlApplicationContext
 * @since 1.4.0
 */
public class InvalidRegionDataPolicyShortcutsTest {

	@Test(expected = BeanCreationException.class)
	public void testInvalidRegionShortcutWithPersistentAttribute() {

		try {
			new ClassPathXmlApplicationContext(
				"/org/springframework/data/gemfire/invalid-region-shortcut-with-persistent-attribute.xml");
		}
		catch (BeanCreationException expected) {

			assertThat(expected).hasMessageContaining("Error creating bean with name 'InvalidReplicate'");

			throw expected;
		}
	}

	@Test(expected = BeanDefinitionParsingException.class)
	public void testInvalidUseOfRegionDataPolicyAndShortcut() {

		try {
			new ClassPathXmlApplicationContext(
				"/org/springframework/data/gemfire/invalid-use-of-region-datapolicy-and-shortcut.xml");
		}
		catch (BeanDefinitionParsingException expected) {

			assertThat(expected).hasMessageContaining(
				"Only one of [data-policy, shortcut] may be specified with element [gfe:partitioned-region]");

			throw expected;
		}
	}

}
