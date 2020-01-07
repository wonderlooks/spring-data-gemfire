/*
 * Copyright 2018-2020 the original author or authors.
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

package org.springframework.data.gemfire.eviction;

import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.FactoryBean;

/**
 * The {@link EvictingRegionFactoryBean} interface specifies {@link Region} {@link FactoryBean FactoryBeans} capable
 * of supporting Eviction configuration, that is, evicting {@link Region} entries.
 *
 * @author John Blum
 * @see org.apache.geode.cache.EvictionAttributes
 * @see org.apache.geode.cache.Region
 * @since 2.1.0
 */
public interface EvictingRegionFactoryBean {

	void setEvictionAttributes(EvictionAttributes evictionAttributes);

}
