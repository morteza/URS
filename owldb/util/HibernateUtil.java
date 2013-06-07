/*
 * This file is part of OWLDB.
 * 
 * OWLDB is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OWLDB is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OWLDB. If not, see <http://www.gnu.org/licenses/>.
 */
package owldb.util;

import java.util.*;
import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.criterion.*;
import org.hibernate.event.*;
import org.hibernate.event.def.*;
import org.semanticweb.owlapi.model.*;
import org.slf4j.*;


/**
 * A Hibernate config utility that allows for change of configuration and manual
 * building of sessionFactory.
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 */
public final class HibernateUtil
{
	private final static Logger	LOGGER	= LoggerFactory.getLogger (HibernateUtil.class);

	private SessionFactory			factory;


	/**
	 * Constructor.
	 * 
	 * @param ontologyManager The owl ontology manager
	 * @param hibernateProperties Additional Hibernate settings
	 */
	public HibernateUtil (final OWLOntologyManager ontologyManager, final Properties hibernateProperties)
	{
		try
		{
			final Configuration configuration = new Configuration ();
			configuration.configure ();
			final Interceptor interceptor = new OWLDBInterceptor (ontologyManager);
			configuration.setInterceptor (interceptor);

			final EventListeners listeners = configuration.getEventListeners ();
			final OWLDBEventListener dbListener = new OWLDBEventListener ();
			listeners.setSaveOrUpdateEventListeners (new SaveOrUpdateEventListener []
			{
					dbListener,
					new DefaultSaveOrUpdateEventListener ()
			});
			listeners.setSaveEventListeners (new SaveOrUpdateEventListener []
			{
					dbListener,
					new DefaultSaveEventListener ()
			});
			listeners.setPostCommitDeleteEventListeners (new PostDeleteEventListener []
			{
				dbListener
			});
			listeners.setPostCommitInsertEventListeners (new PostInsertEventListener []
			{
				dbListener
			});

			// Enable second level cache if not set otherwise
			if (hibernateProperties.get (Environment.USE_SECOND_LEVEL_CACHE) == null)
				hibernateProperties.setProperty (Environment.USE_SECOND_LEVEL_CACHE, "true");
			// Enable query cache if not set otherwise and second level is enabled
			if ("true".equals (hibernateProperties.get (Environment.USE_SECOND_LEVEL_CACHE)) && hibernateProperties.get (Environment.USE_QUERY_CACHE) == null)
				hibernateProperties.setProperty (Environment.USE_QUERY_CACHE, "true");

			// Add additional specific Hibernate properties
			configuration.addProperties (hibernateProperties);

			// Create the SessionFactory from the configuration.
			this.factory = configuration.buildSessionFactory ();
		}
		catch (final Throwable ex)
		{
			LOGGER.error ("Initial SessionFactory creation failed.", ex);
			throw new ExceptionInInitializerError (ex);
		}
	}


	/**
	 * Executes the given "Count"-Query.
	 * 
	 * @param query The query to execute
	 * @return The number of results
	 */
	public Integer countQuery (final String query)
	{
		return this.execute (new HibernateWrapper<Integer> ()
		{
			@Override
			public Integer doInHibernate (final Session session)
			{
				return HibernateUtil.this.countQuery (session.createQuery (query));
			}
		});
	}


	/**
	 * Executes the given "Count"-Query.
	 * 
	 * @param query The query to execute
	 * @return The number of results
	 */
	public Integer countQuery (final Query query)
	{
		final Object count = query.iterate ().next ();
		return count instanceof Integer ? (Integer) count : Integer.valueOf (((Number) count).intValue ());
	}


	/**
	 * Executes the given query. Casts the result list to the type of the given
	 * class.
	 * 
	 * @param <T> The concrete class
	 * @param clazz The class to which to cast
	 * @param query The query to execute
	 * @return The result list
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> retrieveList (final Class<T> clazz, final Query query)
	{
		return query.list ();
	}


	/**
	 * Executes the given criteria. Casts the result list to the type of the given
	 * class.
	 * 
	 * @param <T> The concrete class
	 * @param clazz The class to which to cast
	 * @param query The query to execute
	 * @return The result list
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> retrieveList (final Class<T> clazz, final Criteria query)
	{
		return query.list ();
	}


	/**
	 * Executes the given query. Casts the result list to the type of the given
	 * class.
	 * 
	 * @param <T> The type of the set
	 * @param clazz The clazz
	 * @param query The query to execute
	 * @return All instances
	 */
	public <T> List<T> retrieveList (final Class<T> clazz, final String query)
	{
		return this.execute (new HibernateWrapper<List<T>> ()
		{
			@Override
			public List<T> doInHibernate (final Session session)
			{
				return HibernateUtil.this.retrieveList (clazz, session.createQuery (query));
			}
		});
	}


	/**
	 * Executes the given query. Converts the result to a set of the type of the
	 * given class.
	 * 
	 * @param <T> The concrete class
	 * @param clazz The class to which to cast
	 * @param query The query to execute
	 * @return The result set
	 */
	@SuppressWarnings("unchecked")
	public <T> Set<T> retrieveSet (final Class<T> clazz, final Query query)
	{
		return new HashSet<T> (query.list ());
	}


	/**
	 * Executes the given query. Converts the result to a set of the type of the
	 * given class.
	 * 
	 * @param <T> The concrete class
	 * @param clazz The class to which to cast
	 * @param query The query to execute
	 * @return The result set
	 */
	@SuppressWarnings("unchecked")
	public <T> Set<T> retrieveSet (final Class<T> clazz, final Criteria query)
	{
		return new HashSet<T> (query.list ());
	}


	/**
	 * Retrieve all instances of the given class matching the given query as a set
	 * in a Hibernate transaction.
	 * 
	 * @param <T> The type of the set
	 * @param clazz The clazz
	 * @param query The query to execute
	 * @return All instances
	 */
	public <T> Set<T> retrieveSet (final Class<T> clazz, final String query)
	{
		return this.execute (new HibernateWrapper<Set<T>> ()
		{
			@Override
			public Set<T> doInHibernate (final Session session)
			{
				return HibernateUtil.this.retrieveSet (clazz, session.createQuery (query));
			}
		});
	}


	/**
	 * Retrieve all instances of the given class matching the given query as a set
	 * in a Hibernate transaction.
	 * 
	 * @param <T> The type of the set
	 * @param clazz The clazz
	 * @param queries The queries to execute
	 * @return All instances
	 */
	public <T> Set<T> retrieveSet (final Class<T> clazz, final String... queries)
	{
		return this.execute (new HibernateWrapper<Set<T>> ()
		{
			@Override
			public Set<T> doInHibernate (final Session session)
			{
				final Set<T> result = new HashSet<T> ();
				for (final String query: queries)
					result.addAll (HibernateUtil.this.retrieveList (clazz, session.createQuery (query)));
				return result;
			}
		});
	}


	/**
	 * Retrieve all instances of the given class as a set in a Hibernate
	 * transaction.
	 * 
	 * @param <T> The type of the set
	 * @param clazz The clazz
	 * @return All instances
	 */
	public <T> Set<T> retrieveSet (final Class<T> clazz)
	{
		return this.execute (new HibernateWrapper<Set<T>> ()
		{
			@Override
			public Set<T> doInHibernate (final Session session)
			{
				return HibernateUtil.this.retrieveSet (clazz, session.createCriteria (clazz));
			}
		});
	}


	/**
	 * Retrieve all instances of the given classes which all must implement the
	 * same interface as a set in a Hibernate transaction.
	 * 
	 * @param <I> The type of the interface
	 * @param <T> The type of the set
	 * @param interfaze The interface to implement
	 * @param classes The classes
	 * @return All instances
	 */
	public <I, T extends I> Set<I> retrieveSet (final Class<I> interfaze, final Class<T>... classes)
	{
		return this.execute (new HibernateWrapper<Set<I>> ()
		{
			@Override
			public Set<I> doInHibernate (final Session session)
			{
				final Set<I> result = new HashSet<I> ();
				for (final Class<T> clazz: classes)
					result.addAll (HibernateUtil.this.retrieveList (clazz, session.createCriteria (clazz)));
				return result;
			}
		});
	}


	/**
	 * Retrieve all instances of the given class as a set in a Hibernate
	 * transaction.
	 * 
	 * @param <T> The type of the set
	 * @param session The Hibernate session
	 * @param clazz The clazz
	 * @param associationPath A dot-seperated property path
	 * @param idValue Apply an "equal" constraint to the identifier property
	 *          (associationPath)
	 * @return All instances
	 */
	public <T> Set<T> retrieveSet (final Session session, final Class<T> clazz, final String associationPath, final Object idValue)
	{
		return this.retrieveSet (clazz, session.createCriteria (clazz).createCriteria (associationPath).add (Restrictions.idEq (idValue)));
	}


	/**
	 * Retrieve all instances of the given class as a set in a Hibernate
	 * transaction.
	 * 
	 * @param <T> The type of the set
	 * @param clazz The clazz
	 * @param associationPath A dot-seperated property path
	 * @param idValue Apply an "equal" constraint to the identifier property
	 *          (associationPath)
	 * @return All instances
	 */
	public <T> Set<T> retrieveSet (final Class<T> clazz, final String associationPath, final Object idValue)
	{
		return this.execute (new HibernateWrapper<Set<T>> ()
		{
			@Override
			public Set<T> doInHibernate (final Session session)
			{
				return HibernateUtil.this.retrieveSet (session, clazz, associationPath, idValue);
			}
		});
	}


	/**
	 * Retrieve all instances of the given class which matches the given key value
	 * as a set in a Hibernate transaction.
	 * 
	 * @param <T> The type of the set
	 * @param session The Hibernate session
	 * @param clazz The clazz
	 * @param query The query to execute
	 * @param key The key to match
	 * @return All instances
	 */
	public <T> Set<T> retrieveSetById (final Session session, final Class<T> clazz, final String query, final Long key)
	{
		return this.retrieveSet (clazz, session.createQuery (query).setLong ("id", key.longValue ()));
	}


	/**
	 * Retrieve all instances of the given class which matches the given key value
	 * as a set in a Hibernate transaction.
	 * 
	 * @param <T> The type of the set
	 * @param clazz The clazz
	 * @param query The query to execute
	 * @param key The key to match
	 * @return All instances
	 */
	public <T> Set<T> retrieveSetById (final Class<T> clazz, final String query, final Long key)
	{
		return this.execute (new HibernateWrapper<Set<T>> ()
		{
			@Override
			public Set<T> doInHibernate (final Session session)
			{
				return HibernateUtil.this.retrieveSetById (session, clazz, query, key);
			}
		});
	}


	/**
	 * Retrieve all instances of the given class which matches the given key value
	 * as a set in a Hibernate transaction.
	 * 
	 * @param <T> The type of the set
	 * @param clazz The clazz
	 * @param queries The queries to execute
	 * @param key The key to match
	 * @return All instances
	 */
	public <T> Set<T> retrieveSetById (final Class<T> clazz, final String [] queries, final Long key)
	{
		return this.execute (new HibernateWrapper<Set<T>> ()
		{
			@Override
			public Set<T> doInHibernate (final Session session)
			{
				final Set<T> result = new HashSet<T> ();
				for (final String query: queries)
					result.addAll (HibernateUtil.this.retrieveSetById (session, clazz, query, key));
				return result;
			}
		});
	}


	/**
	 * Tests if an instance matching the given identity check exists.
	 * 
	 * @param session The Hibernate session
	 * @param clazz The clazz of the instance
	 * @param associationPath A dot-seperated property path
	 * @param idValue Apply an "equal" constraint to the identifier property
	 *          (associationPath)
	 * @return True if exists
	 */
	public boolean exists (final Session session, final Class<?> clazz, final String associationPath, final Object idValue)
	{
		return session.createCriteria (clazz).createCriteria (associationPath).add (Restrictions.idEq (idValue)).uniqueResult () != null;
	}


	/**
	 * Tests if an instance matching the given identity check exists.
	 * 
	 * @param clazz The clazz of the instance
	 * @param associationPath A dot-seperated property path
	 * @param idValue Apply an "equal" constraint to the identifier property
	 *          (associationPath)
	 * @return True if exists
	 */
	public boolean exists (final Class<?> clazz, final String associationPath, final Object idValue)
	{
		return this.execute (new HibernateWrapper<Boolean> ()
		{
			@Override
			public Boolean doInHibernate (final Session session)
			{
				return Boolean.valueOf (HibernateUtil.this.exists (session, clazz, associationPath, idValue));
			}
		}).booleanValue ();
	}


	/**
	 * Executes the given Hibernate wrapper.
	 * 
	 * @param <T> The type of the result value
	 * @param wrapper The wrapper to execute
	 * @return The result object
	 */
	public <T> T execute (final HibernateWrapper<T> wrapper)
	{
		return executeTransaction (this.factory, wrapper);
	}


	/**
	 * Executes the given Hibernate wrapper.
	 * 
	 * @param <T> The type of the result value
	 * @param factory A session factory
	 * @param wrapper The wrapper to execute
	 * @return The result object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T executeTransaction (final SessionFactory factory, final HibernateWrapper<T> wrapper)
	{
		final Session session = factory.openSession ();
		session.setFlushMode (FlushMode.COMMIT);
		Transaction t = null;
		try
		{
			t = session.beginTransaction ();
			final Object result = wrapper.doInHibernate (session);
			t.commit ();
			return (T) result;
		}
		catch (final RuntimeException ex)
		{
			if (t != null)
				t.rollback ();
			throw ex;
		}
		finally
		{
			session.close ();
		}
	}


	/**
	 * Destroys the factory object which frees up all resources. You can't use the
	 * instance afterwards.
	 */
	public void destroy ()
	{
		this.factory.close ();
	}
}