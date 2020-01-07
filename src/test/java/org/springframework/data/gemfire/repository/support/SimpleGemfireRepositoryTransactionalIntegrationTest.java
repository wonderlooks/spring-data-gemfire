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

package org.springframework.data.gemfire.repository.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.apache.geode.cache.Region;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.gemfire.mapping.GemfireMappingContext;
import org.springframework.data.gemfire.mapping.GemfirePersistentEntity;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.sample.Customer;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.support.PersistentEntityInformation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Integration tests testing the {@link SimpleGemfireRepository} class and SDC Repository abstraction implementation
 * in the context of Pivotal GemFire"Cache" Transactions.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.springframework.data.gemfire.repository.support.SimpleGemfireRepository
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.6.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class SimpleGemfireRepositoryTransactionalIntegrationTest {

	// TODO add additional test cases for SimpleGemfireRepository (Region operations) in the presence of Transactions!!!

	static final AtomicLong ID_SEQUENCE = new AtomicLong(0L);

	@Autowired
	private CustomerService customerService;

	@Resource(name = "Customers")
	private Region customers;

	static Customer createCustomer(String firstName, String lastName) {

		Customer customer = new SerializableCustomer(firstName, lastName);

		customer.setId(ID_SEQUENCE.incrementAndGet());

		return customer;
	}

	@Before
	public void setup() {

		assertNotNull("The 'Customers' Cache Region was not properly configured and initialized!", this.customers);
		assertEquals("Customers", this.customers.getName());
		assertEquals("/Customers", this.customers.getFullPath());
		assertTrue(this.customers.isEmpty());
	}

	@After
	public void tearDown() {
		this.customers.clear();
	}

	@Test
	public void testDeleteAll() {

		Collection<Customer> expectedCustomers = new ArrayList<>(4);

		expectedCustomers.add(createCustomer("Jon", "Doe"));
		expectedCustomers.add(createCustomer("Jane", "Doe"));
		expectedCustomers.add(createCustomer("Pie", "Doe"));
		expectedCustomers.add(createCustomer("Cookie", "Doe"));

		this.customerService.saveAll(expectedCustomers);

		assertFalse(this.customers.isEmpty());
		assertEquals(expectedCustomers.size(), this.customers.size());

		try {
			this.customerService.removeAllCausingTransactionRollback();
		}
		catch (RuntimeException ignore) {
			// the RuntimeException should cause the Cache Transaction to rollback and avoid the Region modification!
		}

		assertFalse(this.customers.isEmpty());
		assertEquals(expectedCustomers.size(), this.customers.size());

		this.customerService.removeAll();

		assertTrue(this.customers.isEmpty());
	}

	public static class SerializableCustomer extends Customer implements Serializable {

		public SerializableCustomer() { }

		public SerializableCustomer(Long id) {
			super(id);
		}

		public SerializableCustomer(String firstName, String lastName) {
			super(firstName, lastName);
		}
	}

	public static class CustomerService {

		private GemfireRepository<Customer, Long> customerRepository;

		private TransactionTemplate transactionTemplate;

		@Autowired
		@SuppressWarnings("all")
		public CustomerService(GemfireTemplate customersTemplate, PlatformTransactionManager transactionManager) {

			GemfireMappingContext mappingContext = new GemfireMappingContext();

			GemfirePersistentEntity<Customer> customerEntity =
				(GemfirePersistentEntity<Customer>) mappingContext.getPersistentEntity(Customer.class);

			EntityInformation<Customer, Long> entityInformation = new PersistentEntityInformation<>(customerEntity);

			this.customerRepository = new SimpleGemfireRepository<Customer, Long>(customersTemplate, entityInformation);
			this.transactionTemplate = new TransactionTemplate(transactionManager);
		}

		void saveAll(final Iterable<Customer> customers) {

			this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					CustomerService.this.customerRepository.saveAll(customers);
				}
			});
		}

		void removeAllCausingTransactionRollback() {
			this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {

					removeAll();

					throw new IllegalStateException("'removeAll' operation not permitted");
				}
			});
		}

		void removeAll() {

			this.transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					CustomerService.this.customerRepository.deleteAll();
				}
			});
		}
	}
}
