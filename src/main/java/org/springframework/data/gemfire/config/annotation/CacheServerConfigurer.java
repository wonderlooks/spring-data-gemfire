/*
 * Copyright 2016-2020 the original author or authors.
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
package org.springframework.data.gemfire.config.annotation;

import org.apache.geode.cache.server.CacheServer;

import org.springframework.data.gemfire.config.annotation.support.Configurer;
import org.springframework.data.gemfire.server.CacheServerFactoryBean;

/**
 * The {@link CacheServerConfigurer} interface defines a contract for implementing {@link Object Objects} in order to
 * customize the configuration of a {@link CacheServerFactoryBean} used to construct, configure and initialize
 * an instance of {@link CacheServer}.
 *
 * @author John Blum
 * @see java.lang.FunctionalInterface
 * @see org.apache.geode.cache.server.CacheServer
 * @see org.springframework.data.gemfire.config.annotation.CacheServerApplication
 * @see org.springframework.data.gemfire.config.annotation.EnableCacheServer
 * @see org.springframework.data.gemfire.config.annotation.EnableCacheServers
 * @see org.springframework.data.gemfire.config.annotation.support.Configurer
 * @see org.springframework.data.gemfire.server.CacheServerFactoryBean
 * @since 2.0.0
 */
@FunctionalInterface
public interface CacheServerConfigurer extends Configurer<CacheServerFactoryBean> {

}
