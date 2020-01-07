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
package org.springframework.data.gemfire.test;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.cache.asyncqueue.AsyncEventQueue;
import org.apache.geode.cache.asyncqueue.AsyncEventQueueFactory;
import org.apache.geode.cache.wan.GatewayEventFilter;
import org.apache.geode.cache.wan.GatewayEventSubstitutionFilter;
import org.apache.geode.cache.wan.GatewaySender;
import org.apache.geode.cache.wan.GatewaySender.OrderPolicy;

/**
 * @author David Turanski
 * @author John Blum
 */
@SuppressWarnings("deprecated")
public class StubAsyncEventQueueFactory implements AsyncEventQueueFactory {

	private AsyncEventQueue asyncEventQueue = mock(AsyncEventQueue.class);

	private boolean batchConflationEnabled;
	private boolean diskSynchronous;
	private boolean forwardExpirationDestroy;
	private boolean parallel;
	private boolean pauseEventDispatching;
	private boolean persistent;

	private int batchSize;
	private int batchTimeInterval;
	private int dispatcherThreads = GatewaySender.DEFAULT_DISPATCHER_THREADS;
	private int maxQueueMemory;

	private GatewayEventSubstitutionFilter<?, ?> gatewayEventSubstitutionFilter;

	private List<GatewayEventFilter> gatewayEventFilters = new ArrayList<>();

	private OrderPolicy orderPolicy;

	private String diskStoreName;

	@Override
	public AsyncEventQueue create(String name, AsyncEventListener listener) {

		when(asyncEventQueue.getAsyncEventListener()).thenReturn(listener);
		when(asyncEventQueue.getBatchSize()).thenReturn(this.batchSize);
		when(asyncEventQueue.getDiskStoreName()).thenReturn(this.diskStoreName);
		when(asyncEventQueue.isPersistent()).thenReturn(this.persistent);
		when(asyncEventQueue.getId()).thenReturn(name);
		when(asyncEventQueue.getMaximumQueueMemory()).thenReturn(this.maxQueueMemory);
		when(asyncEventQueue.isParallel()).thenReturn(this.parallel);
		when(asyncEventQueue.isBatchConflationEnabled()).thenReturn(this.batchConflationEnabled);
		when(asyncEventQueue.isDiskSynchronous()).thenReturn(this.diskSynchronous);
		when(asyncEventQueue.getBatchTimeInterval()).thenReturn(this.batchTimeInterval);
		when(asyncEventQueue.getOrderPolicy()).thenReturn(this.orderPolicy);
		when(asyncEventQueue.getDispatcherThreads()).thenReturn(this.dispatcherThreads);
		when(asyncEventQueue.isDispatchingPaused()).thenAnswer(invocation -> this.pauseEventDispatching);
		when(asyncEventQueue.getGatewayEventFilters()).thenReturn(Collections.unmodifiableList(gatewayEventFilters));
		when(asyncEventQueue.getGatewayEventSubstitutionFilter()).thenReturn(this.gatewayEventSubstitutionFilter);
		when(asyncEventQueue.getGatewayEventFilters()).thenReturn(Collections.unmodifiableList(gatewayEventFilters));
		when(asyncEventQueue.isForwardExpirationDestroy()).thenReturn(this.forwardExpirationDestroy);

		doAnswer(invocation -> {
			this.pauseEventDispatching = false;
			return null;
		}).when(asyncEventQueue).resumeEventDispatching();

		return this.asyncEventQueue;
	}

	//The following added in 7.0.1
	public AsyncEventQueueFactory setBatchConflationEnabled(boolean arg0) {
		this.batchConflationEnabled = arg0;
		return this;
	}

	@Override
	public AsyncEventQueueFactory setBatchSize(int batchSize) {
		this.batchSize = batchSize;
		return this;
	}

	@Override
	public AsyncEventQueueFactory setBatchTimeInterval(int interval) {
		this.batchTimeInterval = interval;
		return this;
	}

	@Override
	public AsyncEventQueueFactory setDiskStoreName(String diskStoreName) {
		this.diskStoreName = diskStoreName;
		return this;
	}

	public AsyncEventQueueFactory setDiskSynchronous(boolean arg0) {
		this.diskSynchronous = arg0;
		return this;
	}

	public AsyncEventQueueFactory setDispatcherThreads(int arg0) {
		this.dispatcherThreads = arg0;
		return this;
	}

	@Override
	public AsyncEventQueueFactory setForwardExpirationDestroy(boolean forward) {
		this.forwardExpirationDestroy = forward;
		return this;
	}

	public AsyncEventQueueFactory setGatewayEventSubstitutionListener(final GatewayEventSubstitutionFilter gatewayEventSubstitutionFilter) {
		this.gatewayEventSubstitutionFilter = gatewayEventSubstitutionFilter;
		return this;
	}

	public AsyncEventQueueFactory setMaximumQueueMemory(int maxQueueMemory) {
		this.maxQueueMemory = maxQueueMemory;
		return this;
	}

	public AsyncEventQueueFactory setOrderPolicy(OrderPolicy arg0) {
		this.orderPolicy = arg0;
		return this;
	}

	@Override
	public AsyncEventQueueFactory setParallel(boolean parallel) {
		this.parallel = parallel;
		return this;
	}

	@Override
	public AsyncEventQueueFactory setPersistent(boolean persistent) {
		this.persistent = persistent;
		return this;
	}

	@Override
	public AsyncEventQueueFactory addGatewayEventFilter(final GatewayEventFilter gatewayEventFilter) {
		gatewayEventFilters.add(gatewayEventFilter);
		return this;
	}

	@Override
	public AsyncEventQueueFactory pauseEventDispatching() {
		this.pauseEventDispatching = true;
		return this;
	}

	@Override
	public AsyncEventQueueFactory removeGatewayEventFilter(final GatewayEventFilter gatewayEventFilter) {
		gatewayEventFilters.remove(gatewayEventFilter);
		return this;
	}
}
