/*
 * Copyright 2012-2020 the original author or authors.
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
 *
 */

package org.springframework.data.gemfire.util;

import java.util.Optional;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.internal.cache.GemFireCacheImpl;

import org.springframework.util.StringUtils;

/**
 * {@link CacheUtils} is an abstract utility class encapsulating common operations for working with
 * {@link Cache} and {@link ClientCache} instances.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.CacheFactory
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.ClientCacheFactory
 * @see org.apache.geode.distributed.DistributedSystem
 * @see org.apache.geode.internal.cache.GemFireCacheImpl
 * @see org.springframework.data.gemfire.util.DistributedSystemUtils
 * @since 1.8.0
 */
@SuppressWarnings("unused")
public abstract class CacheUtils extends DistributedSystemUtils {

	public static final String DEFAULT_POOL_NAME = "DEFAULT";

	@SuppressWarnings("all")
	public static boolean isClient(GemFireCache cache) {

		boolean client = (cache instanceof ClientCache);

		if (cache instanceof GemFireCacheImpl) {
			client &= ((GemFireCacheImpl) cache).isClient();
		}

		return client;
	}

	public static boolean isDefaultPool(Pool pool) {
		return Optional.ofNullable(pool).map(Pool::getName).filter(CacheUtils::isDefaultPool).isPresent();
	}

	public static boolean isNotDefaultPool(Pool pool) {
		return !isDefaultPool(pool);
	}

	public static boolean isDefaultPool(String poolName) {
		return DEFAULT_POOL_NAME.equals(poolName);
	}

	public static boolean isNotDefaultPool(String poolName) {
		return !isDefaultPool(poolName);
	}

	public static boolean isDurable(ClientCache clientCache) {

		// NOTE: Technically, the following code snippet would be more useful/valuable but is not "testable"!
		//((InternalDistributedSystem) distributedSystem).getConfig().getDurableClientId();

		return Optional.ofNullable(clientCache)
			.<DistributedSystem>map(CacheUtils::getDistributedSystem)
			.filter(DistributedSystem::isConnected)
			.map(DistributedSystem::getProperties)
			.map(properties -> properties.getProperty(DURABLE_CLIENT_ID_PROPERTY_NAME, null))
			.filter(StringUtils::hasText)
			.isPresent();
	}

	@SuppressWarnings("all")
	public static boolean isPeer(GemFireCache cache) {

		boolean peer = (cache instanceof Cache);

		if (cache instanceof GemFireCacheImpl) {
			peer &= !((GemFireCacheImpl) cache).isClient();
		}

		return peer;
	}

	public static boolean close() {
		return close(resolveGemFireCache());
	}

	public static boolean close(GemFireCache gemfireCache) {
		return close(gemfireCache, () -> {});
	}

	public static boolean close(GemFireCache gemfireCache, Runnable shutdownHook) {

		try {
			gemfireCache.close();
			return true;
		}
		catch (Exception ignore) {
			return false;
		}
		finally {
			Optional.ofNullable(shutdownHook).ifPresent(Runnable::run);
		}
	}

	public static boolean closeCache() {

		try {
			CacheFactory.getAnyInstance().close();
			return true;
		}
		catch (Exception ignore) {
			return false;
		}
	}

	public static boolean closeClientCache() {

		try {
			ClientCacheFactory.getAnyInstance().close();
			return true;
		}
		catch (Exception ignore) {
			return false;
		}
	}

	public static Cache getCache() {

		try {
			return CacheFactory.getAnyInstance();
		}
		catch (CacheClosedException ignore) {
			return null;
		}
	}

	public static ClientCache getClientCache() {

		try {
			return ClientCacheFactory.getAnyInstance();
		}
		catch (CacheClosedException | IllegalStateException ignore) {
			return null;
		}
	}

	public static GemFireCache resolveGemFireCache() {
		return Optional.<GemFireCache>ofNullable(getClientCache()).orElseGet(CacheUtils::getCache);
	}
}
