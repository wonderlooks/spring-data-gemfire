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

import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.util.StringUtils;

/**
 * Abstract utility class encapsulating functionality common to {@link Object Objects}, {@link Class Class types}
 * and Spring beans.
 *
 * @author John Blum
 * @see java.lang.Class
 * @see java.lang.Object
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.config.BeanDefinition
 * @see org.springframework.beans.factory.config.RuntimeBeanReference
 * @since 1.8.0
 */
@SuppressWarnings("unused")
public abstract class SpringUtils {

	public static BeanDefinition addDependsOn(BeanDefinition beanDefinition, String... beanNames) {

		List<String> dependsOnList = new ArrayList<>();

		Collections.addAll(dependsOnList, nullSafeArray(beanDefinition.getDependsOn(), String.class));
		dependsOnList.addAll(Arrays.asList(nullSafeArray(beanNames, String.class)));
		beanDefinition.setDependsOn(dependsOnList.toArray(new String[0]));

		return beanDefinition;
	}

	public static Optional<Object> getPropertyValue(BeanDefinition beanDefinition, String propertyName) {

		return Optional.ofNullable(beanDefinition)
			.map(BeanDefinition::getPropertyValues)
			.map(propertyValues -> propertyValues.getPropertyValue(propertyName))
			.map(PropertyValue::getValue);
	}

	public static BeanDefinition setPropertyReference(BeanDefinition beanDefinition,
			String propertyName, String beanName) {

		beanDefinition.getPropertyValues().addPropertyValue(propertyName, new RuntimeBeanReference(beanName));

		return beanDefinition;
	}

	public static BeanDefinition setPropertyValue(BeanDefinition beanDefinition,
			String propertyName, Object propertyValue) {

		beanDefinition.getPropertyValues().addPropertyValue(propertyName, propertyValue);

		return beanDefinition;
	}

	public static String defaultIfEmpty(String value, String defaultValue) {
		return defaultIfEmpty(value, () -> defaultValue);
	}

	public static String defaultIfEmpty(String value, Supplier<String> supplier) {
		return StringUtils.hasText(value) ? value : supplier.get();
	}

	public static <T> T defaultIfNull(T value, T defaultValue) {
		return defaultIfNull(value, () -> defaultValue);
	}

	public static <T> T defaultIfNull(T value, Supplier<T> supplier) {
		return value != null ? value : supplier.get();
	}

	public static String dereferenceBean(String beanName) {
		return String.format("%1$s%2$s", BeanFactory.FACTORY_BEAN_PREFIX, beanName);
	}

	public static boolean equalsIgnoreNull(Object obj1, Object obj2) {
		return Objects.equals(obj1, obj2);
	}

	public static boolean nullOrEquals(Object obj1, Object obj2) {
		return obj1 == null || obj1.equals(obj2);
	}

	public static boolean nullSafeEquals(Object obj1, Object obj2) {
		return obj1 != null && obj1.equals(obj2);
	}

	public static String nullSafeName(Class<?> type) {
		return type != null ? type.getName() : null;
	}

	public static String nullSafeSimpleName(Class<?> type) {
		return type != null ? type.getSimpleName() : null;
	}

	public static boolean safeDoOperation(VoidReturningThrowableOperation operation) {

		try {
			operation.run();
			return true;
		}
		catch (Throwable cause) {
			return false;
		}
	}

	public static <T> T safeGetValue(Supplier<T> valueSupplier) {
		return safeGetValue(valueSupplier, (T) null);
	}

	public static <T> T safeGetValue(Supplier<T> valueSupplier, T defaultValue) {
		return safeGetValue(valueSupplier, (Supplier<T>) () -> defaultValue);
	}

	public static <T> T safeGetValue(Supplier<T> valueSupplier, Supplier<T> defaultValueSupplier) {
		return safeGetValue(valueSupplier, (Function<Throwable, T>) exception -> defaultValueSupplier.get());
	}

	public static <T> T safeGetValue(Supplier<T> valueSupplier, Function<Throwable, T> exceptionHandler) {

		try {
			return valueSupplier.get();
		}
		catch (Throwable cause) {
			return exceptionHandler.apply(cause);
		}
	}

	public static void safeRunOperation(VoidReturningThrowableOperation operation) {
		safeRunOperation(operation, cause -> new InvalidDataAccessApiUsageException("Failed to run operation", cause));
	}

	public static void safeRunOperation(VoidReturningThrowableOperation operation,
			Function<Throwable, RuntimeException> exceptionConverter) {

		try {
			operation.run();
		}
		catch (Throwable cause) {
			throw exceptionConverter.apply(cause);
		}
	}

	/**
	 * @deprecated use {@link VoidReturningThrowableOperation}.
	 */
	@Deprecated
	public interface VoidReturningExceptionThrowingOperation extends VoidReturningThrowableOperation { }

	@FunctionalInterface
	public interface VoidReturningThrowableOperation {
		void run() throws Throwable;
	}
}
