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

package org.springframework.data.gemfire.function.sample;

import org.springframework.data.gemfire.function.annotation.OnMember;

/**
 * The HelloFunctionExecution interface is a SDG Function Execution interface
 * for the 'hello' Pivotal GemFireFunction and hello greetings.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.function.annotation.OnMember
 * @since 1.7.0
 */
@OnMember(groups = "HelloGroup")
@SuppressWarnings("unused")
public interface HelloFunctionExecution {

	String hello(String addressTo);

}
