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

package org.springframework.data.gemfire.config.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;

import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.apache.geode.cache.util.CacheWriterAdapter;
import org.apache.geode.cache.util.ObjectSizer;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

/**
 * The TemplateClientRegionNamespaceTest class is a test suite of test cases testing the contract and functionality
 * of Client Region Templates using SDG XML namespace configuration meta-data.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.springframework.data.gemfire.test.GemfireTestApplicationContextInitializer
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.Region
 * @since 1.5.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class TemplateClientRegionNamespaceTest {

	@Resource(name = "TemplateBasedClientRegion")
	private Region<Integer, Object> templateBasedClientRegion;

	private void assertCacheListeners(Region<?, ?> region, String... expectedNames) {

		assertNotNull(region);
		assertNotNull(region.getAttributes());
		assertNotNull(region.getAttributes().getCacheListeners());
		assertEquals(expectedNames.length, region.getAttributes().getCacheListeners().length);

		for (CacheListener cacheListener : region.getAttributes().getCacheListeners()) {
			assertTrue(cacheListener instanceof TestCacheListener);
			assertTrue(Arrays.asList(expectedNames).contains(cacheListener.toString()));
		}
	}

	private void assertCacheLoader(Region<?, ?> region, String expectedName) {
		assertNotNull(region);
		assertNotNull(region.getAttributes());
		assertTrue(region.getAttributes().getCacheLoader() instanceof TestCacheLoader);
		assertEquals(expectedName, region.getAttributes().getCacheLoader().toString());
	}

	private void assertCacheWriter(Region<?, ?> region, String expectedName) {
		assertNotNull(region);
		assertNotNull(region.getAttributes());
		assertTrue(region.getAttributes().getCacheWriter() instanceof TestCacheWriter);
		assertEquals(expectedName, region.getAttributes().getCacheWriter().toString());
	}

	private void assertDefaultEvictionAttributes(EvictionAttributes evictionAttributes) {
		assumeNotNull(evictionAttributes);
		assertEvictionAttributes(evictionAttributes, EvictionAction.NONE, EvictionAlgorithm.NONE, 0, null);
	}

	private void assertEvictionAttributes(EvictionAttributes evictionAttributes, EvictionAction expectedAction,
			EvictionAlgorithm expectedAlgorithm, int expectedMaximum, ObjectSizer expectedObjectSizer) {

		assertNotNull("The 'EvictionAttributes' must not be null!", evictionAttributes);
		assertEquals(expectedAction, evictionAttributes.getAction());
		assertEquals(expectedAlgorithm, evictionAttributes.getAlgorithm());
		assertEquals(expectedMaximum, evictionAttributes.getMaximum());
		assertEquals(expectedObjectSizer, evictionAttributes.getObjectSizer());
	}

	private void assertDefaultExpirationAttributes(ExpirationAttributes expirationAttributes) {
		assumeNotNull(expirationAttributes);
		assertEquals(ExpirationAction.INVALIDATE, expirationAttributes.getAction());
		assertEquals(0, expirationAttributes.getTimeout());
	}

	private void assertExpirationAttributes(ExpirationAttributes expirationAttributes, ExpirationAction expectedAction,
			int expectedTimeout) {

		assertNotNull("The 'ExpirationAttributes' must not be null!", expirationAttributes);
		assertEquals(expectedAction, expirationAttributes.getAction());
		assertEquals(expectedTimeout, expirationAttributes.getTimeout());
	}

	private void assertDefaultRegionAttributes(Region region) {

		assertNotNull("The Region must not be null!", region);
		assertNotNull(String.format("The Region (%1$s) must have 'RegionAttributes' defined!",
			region.getFullPath()), region.getAttributes());
		assertNull(region.getAttributes().getCompressor());
		assertNull(region.getAttributes().getCustomEntryIdleTimeout());
		assertNull(region.getAttributes().getCustomEntryTimeToLive());
		assertNull(region.getAttributes().getDiskStoreName());
		assertFalse(region.getAttributes().getMulticastEnabled());
		assertDefaultExpirationAttributes(region.getAttributes().getRegionTimeToLive());
		assertDefaultExpirationAttributes(region.getAttributes().getRegionIdleTimeout());
	}

	private static void assertEmpty(Object[] array) {
		assertTrue((array == null || array.length == 0));
	}

	private static void assertEmpty(Iterable<?> collection) {
		assertTrue(collection == null || !collection.iterator().hasNext());
	}

	private static void assertNullEmpty(String value) {
		assertFalse(StringUtils.hasText(value));
	}

	private static void assertRegionMetaData(Region<?, ?> region, String expectedRegionName) {
		assertRegionMetaData(region, expectedRegionName, Region.SEPARATOR + expectedRegionName);
	}

	private static void assertRegionMetaData(Region<?, ?> region, String expectedRegionName, String expectedRegionPath) {

		assertNotNull(String.format("The '%1$s' Region was not properly configured and initialized!",
			expectedRegionName), region);
		assertEquals(expectedRegionName, region.getName());
		assertEquals(expectedRegionPath, region.getFullPath());
		assertNotNull(String.format("The '%1$s' Region must have RegionAttributes defined!",
			expectedRegionName), region.getAttributes());
	}

	@Test
	public void testTemplateBasedClientRegion() {

		assertRegionMetaData(templateBasedClientRegion, "TemplateBasedClientRegion");
		assertDefaultRegionAttributes(templateBasedClientRegion);
		assertCacheListeners(templateBasedClientRegion, "XYZ");
		assertCacheLoader(templateBasedClientRegion, "A");
		assertCacheWriter(templateBasedClientRegion, "B");
		assertFalse(templateBasedClientRegion.getAttributes().getCloningEnabled());
		assertFalse(templateBasedClientRegion.getAttributes().getConcurrencyChecksEnabled());
		assertEquals(16, templateBasedClientRegion.getAttributes().getConcurrencyLevel());
		assertEquals(DataPolicy.NORMAL, templateBasedClientRegion.getAttributes().getDataPolicy());
		assertFalse(templateBasedClientRegion.getAttributes().isDiskSynchronous());
		assertEvictionAttributes(templateBasedClientRegion.getAttributes().getEvictionAttributes(),
			EvictionAction.OVERFLOW_TO_DISK, EvictionAlgorithm.LRU_ENTRY, 1024, null);
		assertEquals(51, templateBasedClientRegion.getAttributes().getInitialCapacity());
		assertEquals(Integer.class, templateBasedClientRegion.getAttributes().getKeyConstraint());
		assertEquals("0.85", String.valueOf(templateBasedClientRegion.getAttributes().getLoadFactor()));
		assertEquals("ServerPool", templateBasedClientRegion.getAttributes().getPoolName());
		assertTrue(templateBasedClientRegion.getAttributes().getStatisticsEnabled());
		assertEquals(Object.class, templateBasedClientRegion.getAttributes().getValueConstraint());
		templateBasedClientRegion.getInterestList();
	}

	public static final class TestCacheListener extends CacheListenerAdapter {

		private String name;

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static final class TestCacheLoader implements CacheLoader {

		private String name;

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public Object load(LoaderHelper loaderHelper) throws CacheLoaderException {
			return null;
		}

		@Override
		public void close() { }

		@Override
		public String toString() {
			return name;
		}
	}

	public static final class TestCacheWriter extends CacheWriterAdapter {

		private String name;

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
