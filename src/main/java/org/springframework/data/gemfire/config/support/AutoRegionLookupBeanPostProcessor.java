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

package org.springframework.data.gemfire.config.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * The {@link AutoRegionLookupBeanPostProcessor} class is a Spring {@link BeanPostProcessor} that post processes
 * a {@link GemFireCache} by registering all cache {@link Region Regions} that have not been explicitly defined
 * in the Spring application context.
 *
 * This is usually the case for {@link Region Regions} that have been defined in Pivotal GemFire's
 * native {@literal cache.xml} or defined using Pivotal GemFire's Cluster Configuration Service.
 *
 * @author John Blum
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @since 1.5.0
 */
public class AutoRegionLookupBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

	private ConfigurableListableBeanFactory beanFactory;

	/**
	 * Sets a reference to the configured Spring {@link BeanFactory}.
	 *
	 * @param beanFactory configured Spring {@link BeanFactory}.
	 * @throws IllegalArgumentException if the given {@link BeanFactory} is not an instance of
	 * {@link ConfigurableListableBeanFactory}.
	 * @see org.springframework.beans.factory.BeanFactoryAware
	 * @see org.springframework.beans.factory.BeanFactory
	 */
	@Override
	@SuppressWarnings("all")
	public final void setBeanFactory(BeanFactory beanFactory) throws BeansException {

		Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory,
			String.format("BeanFactory [%1$s] must be an instance of %2$s",
				ObjectUtils.nullSafeClassName(beanFactory), ConfigurableListableBeanFactory.class.getSimpleName()));

		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

	/**
	 * Returns a reference to the containing Spring {@link BeanFactory}.
	 *
	 * @return a reference to the containing Spring {@link BeanFactory}.
	 * @throws IllegalStateException if the {@link BeanFactory} was not configured.
	 * @see org.springframework.beans.factory.BeanFactory
	 */
	protected ConfigurableListableBeanFactory getBeanFactory() {
		return Optional.ofNullable(this.beanFactory)
			.orElseThrow(() -> newIllegalStateException("BeanFactory was not properly configured"));
	}

	@SuppressWarnings("all") @Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		if (bean instanceof GemFireCache) {
			registerCacheRegionsAsBeans((GemFireCache) bean);
		}

		return bean;
	}

	void registerCacheRegionsAsBeans(GemFireCache cache) {
		cache.rootRegions().forEach(this::registerCacheRegionAsBean);
	}

	void registerCacheRegionAsBean(Region<?, ?> region) {

		if (region != null) {

			String regionBeanName = getBeanName(region);

			if (!getBeanFactory().containsBean(regionBeanName)) {
				getBeanFactory().registerSingleton(regionBeanName, region);
			}

			for (Region<?, ?> subregion : nullSafeSubregions(region)) {
				registerCacheRegionAsBean(subregion);
			}
		}
	}

	String getBeanName(Region region) {

		return Optional.ofNullable(region.getFullPath())
			.filter(StringUtils::hasText)
			.filter(regionFullPath -> regionFullPath.lastIndexOf(Region.SEPARATOR) > 0)
			.orElseGet(region::getName);
	}

	Set<Region<?, ?>> nullSafeSubregions(Region<?, ?> parentRegion) {
		return Optional.ofNullable(parentRegion.subregions(false)).orElse(Collections.emptySet());
	}
}
