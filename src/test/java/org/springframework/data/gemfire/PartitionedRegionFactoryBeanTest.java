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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.RegionFactory;

import org.junit.Test;

/**
 * Unit Tests for {@link PartitionedRegionFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.DataPolicy
 * @see org.apache.geode.cache.RegionFactory
 * @see org.springframework.data.gemfire.PartitionedRegionFactoryBean
 * @since 1.3.3
 */
@SuppressWarnings("unchecked")
public class PartitionedRegionFactoryBeanTest {

	private final PartitionedRegionFactoryBean factoryBean = new PartitionedRegionFactoryBean();

	protected RegionFactory<?, ?> createMockRegionFactory() {
		return mock(RegionFactory.class);
	}

	@Test
	public void testResolveDataPolicyWithPersistentUnspecifiedAndDataPolicyUnspecified() {
		RegionFactory mockRegionFactory = createMockRegionFactory();
		factoryBean.resolveDataPolicy(mockRegionFactory, null, (String) null);
		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PARTITION));
	}

	@Test
	public void testResolveDataPolicyWhenNotPersistentAndDataPolicyUnspecified() {
		RegionFactory mockRegionFactory = createMockRegionFactory();
		factoryBean.setPersistent(false);
		factoryBean.resolveDataPolicy(mockRegionFactory, false, (String) null);
		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PARTITION));
	}

	@Test
	public void testResolveDataPolicyWhenPersistentAndDataPolicyUnspecified() {
		RegionFactory mockRegionFactory = createMockRegionFactory();
		factoryBean.setPersistent(true);
		factoryBean.resolveDataPolicy(mockRegionFactory, true, (String) null);
		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWithBlankDataPolicy() {
		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.resolveDataPolicy(mockRegionFactory, null, "  ");
		}
		catch (IllegalArgumentException e) {
			assertEquals("Data Policy [  ] is invalid.", e.getMessage());
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.NORMAL));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_REPLICATE));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PRELOADED));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWithEmptyDataPolicy() {
		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.resolveDataPolicy(mockRegionFactory, null, "");
		}
		catch (IllegalArgumentException e) {
			assertEquals("Data Policy [] is invalid.", e.getMessage());
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.NORMAL));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_REPLICATE));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PRELOADED));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWithInvalidDataPolicyName() {
		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.resolveDataPolicy(mockRegionFactory, null, "INVALID_DATA_POLICY_NAME");
		}
		catch (IllegalArgumentException e) {
			assertEquals("Data Policy [INVALID_DATA_POLICY_NAME] is invalid.", e.getMessage());
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PARTITION));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWithInvalidDataPolicyType() {
		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.resolveDataPolicy(mockRegionFactory, null, "REPLICATE");
		}
		catch (IllegalArgumentException e) {
			assertEquals("Data Policy [REPLICATE] is not supported in Partitioned Regions.", e.getMessage());
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.REPLICATE));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PARTITION));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
		}
	}

	@Test
	public void testResolveDataPolicyWhenPersistentUnspecifiedAndPartitionDataPolicy() {
		RegionFactory mockRegionFactory = createMockRegionFactory();
		factoryBean.resolveDataPolicy(mockRegionFactory, null, "PARTITION");
		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PARTITION));
	}

	@Test
	public void testResolveDataPolicyWhenNotPersistentAndPartitionDataPolicy() {
		RegionFactory mockRegionFactory = createMockRegionFactory();
		factoryBean.setPersistent(false);
		factoryBean.resolveDataPolicy(mockRegionFactory, false, "PARTITION");
		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PARTITION));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWhenPersistentAndPartitionDataPolicy() {
		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.setPersistent(true);
			factoryBean.resolveDataPolicy(mockRegionFactory, true, "PARTITION");
		}
		catch (IllegalArgumentException e) {
			assertEquals("Data Policy [PARTITION] is not valid when persistent is true", e.getMessage());
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PARTITION));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
		}
	}

	@Test
	public void testResolveDataPolicyWhenPersistentUnspecifiedAndPersistentPartitionDataPolicy() {
		RegionFactory mockRegionFactory = createMockRegionFactory();
		factoryBean.resolveDataPolicy(mockRegionFactory, null, "PERSISTENT_PARTITION");
		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testResolveDataPolicyWhenNotPersistentAndPersistentPartitionedDataPolicy() {
		RegionFactory mockRegionFactory = createMockRegionFactory();

		try {
			factoryBean.setPersistent(false);
			factoryBean.resolveDataPolicy(mockRegionFactory, false, "PERSISTENT_PARTITION");
		}
		catch (IllegalArgumentException e) {
			assertEquals("Data Policy [PERSISTENT_PARTITION] is not valid when persistent is false", e.getMessage());
			throw e;
		}
		finally {
			verify(mockRegionFactory, never()).setDataPolicy(null);
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PARTITION));
			verify(mockRegionFactory, never()).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
		}
	}

	@Test
	public void testResolveDataPolicyWhenPersistentAndPersistentPartitionedDataPolicy() {
		RegionFactory mockRegionFactory = createMockRegionFactory();
		factoryBean.setPersistent(true);
		factoryBean.resolveDataPolicy(mockRegionFactory, true, "PERSISTENT_PARTITION");
		verify(mockRegionFactory).setDataPolicy(eq(DataPolicy.PERSISTENT_PARTITION));
	}
}
