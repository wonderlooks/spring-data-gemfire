/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;

import org.springframework.util.Assert;

/**
 * Constructs an {@link Execution} using {@link FunctionService#onMember(String...)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.apache.geode.cache.execute.Execution
 * @see org.apache.geode.cache.execute.Function
 * @see org.apache.geode.cache.execute.FunctionService
 * @see org.springframework.data.gemfire.function.execution.AbstractFunctionExecution
 */
class GroupMemberFunctionExecution extends AbstractFunctionExecution {

	private final String[] groups;

	/**
	 * Constructs a new instance of the {@link GroupMemberFunctionExecution} initialized to execute a data independent
	 * {@link Function} on a single member from each of the specified groups.
	 *
	 * @param groups array of {@link String groups} from which to pick a member from each group
	 * on which to execute the data independent {@link Function}.
	 * @throws IllegalArgumentException if {@link String groups} is {@literal null} or empty.
	 */
	public GroupMemberFunctionExecution(String... groups) {

		Assert.notEmpty(groups, "Groups must not be null or empty");

		this.groups = groups;
	}

	protected String[] getGroups() {
		return this.groups;
	}

	/**
	 * Executes the data independent Function on a single member from each of the specified groups.
	 *
	 * @return an Execution to execute the Function.
	 * @see org.apache.geode.cache.execute.FunctionService#onMember(String...)
	 */
	@Override
	protected Execution getExecution() {
		return FunctionService.onMember(getGroups());
	}
}
