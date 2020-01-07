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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.apache.geode.cache.InterestPolicy;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for {@link InterestPolicyConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.InterestPolicyConverter
 * @see org.apache.geode.cache.InterestPolicy
 * @since 1.6.0
 */
public class InterestPolicyConverterUnitTests {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private InterestPolicyConverter converter = new InterestPolicyConverter();

	@After
	public void tearDown() {
		converter.setValue(null);
	}

	@Test
	public void convert() {
		assertThat(converter.convert("all")).isEqualTo(InterestPolicy.ALL);
		assertThat(converter.convert("Cache_Content")).isEqualTo(InterestPolicy.CACHE_CONTENT);
		assertThat(converter.convert("CACHE_ConTent")).isEqualTo(InterestPolicy.CACHE_CONTENT);
		assertThat(converter.convert("ALL")).isEqualTo(InterestPolicy.ALL);
	}

	@Test
	public void convertIllegalValue() {
		exception.expect(IllegalArgumentException.class);
		exception.expectCause(is(nullValue(Throwable.class)));
		exception.expectMessage("[invalid_value] is not a valid InterestPolicy");

		converter.convert("invalid_value");
	}

	@Test
	public void setAsText() {
		assertThat(converter.getValue()).isNull();
		converter.setAsText("aLl");
		assertThat(converter.getValue()).isEqualTo(InterestPolicy.ALL);
		converter.setAsText("Cache_CoNTeNT");
		assertThat(converter.getValue()).isEqualTo(InterestPolicy.CACHE_CONTENT);
	}

	@Test
	public void setAsTextWithInvalidValue() {
		try {
			exception.expect(IllegalArgumentException.class);
			exception.expectCause(is(nullValue(Throwable.class)));
			exception.expectMessage("[none] is not a valid InterestPolicy");

			converter.setAsText("none");
		}
		finally {
			assertThat(converter.getValue()).isNull();
		}
	}
}
