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

import owldb.meta.*;
import java.io.*;
import java.util.*;
import org.hibernate.*;
import org.hibernate.collection.*;
import org.hibernate.engine.*;
import org.hibernate.event.*;
import org.hibernate.persister.collection.*;
import org.hibernate.persister.entity.*;
import org.hibernate.type.*;
import org.hibernate.type.TypeHelper;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;


/**
 * An EventListener that attaches unproxied objects back to the session based on
 * the primary key which is the hashcode.
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 */
public class OWLDBEventListener implements SaveOrUpdateEventListener, PostDeleteEventListener, PostInsertEventListener
{
	private static final long	serialVersionUID	= -1755261821092183249L;


	/** {@inheritDoc} */
	@Override
	public void onSaveOrUpdate (final SaveOrUpdateEvent event)
	{
		final SessionImplementor source = event.getSession ();
		final Object object = event.getObject ();

		// Try to find an object by its hashcode and bind it to the session when
		// found.
		if (source.getContextEntityIdentifier (object) != null)
			return;

		// Only OWLOBject
		if (!(object instanceof OWLObject) || object instanceof OWLOntologyImpl)
			return;

		final Object result = HibernateUtil.executeTransaction (source.getFactory (), new HibernateWrapper<OWLObject> ()
		{
			@Override
			public OWLObject doInHibernate (final Session session)
			{
				return AnnotatedOWLObject.getOWLObjectFromDB (session, object);
			}
		});
		if (result != null)
			this.reassociateObject (source, object, result, Status.MANAGED);
	}


	/**
	 * Reassociate an Object to the Session provided.
	 * 
	 * @param source The session implementor source
	 * @param object Worked object
	 * @param result The loaded object
	 * @param status The Hibernate status
	 */
	private void reassociateObject (final SessionImplementor source, final Object object, final Object result, final Status status)
	{
		final Serializable id = result instanceof OWLObject ? this.getID (source, (OWLObject) result) : source.getContextEntityIdentifier (result);
		if (id == null)
			return;

		final EntityPersister persister = source.getEntityPersister (null, object);
		final EntityMode entityMode = source.getEntityMode ();
		final EntityKey key = new EntityKey (id, persister, entityMode);

		// Get a snapshot
		final Type [] types = persister.getPropertyTypes ();
		final Object [] resultValues = persister.getPropertyValues (result, entityMode);
		final Object [] values = persister.getPropertyValues (object, entityMode);

		if (persister.hasCollections ())
		{
			for (int i = 0; i < types.length; i++)
			{
				if (!types[i].isCollectionType ())
					continue;

				if (values[i] instanceof Collection<?>)
				{
					final Collection<?> unsavedCol = (Collection<?>) values[i];
					final CollectionType collectionType = (CollectionType) types[i];
					final CollectionPersister colPersister = source.getFactory ().getCollectionPersister (collectionType.getRole ());
					final PersistenceContext persistenceContext = source.getPersistenceContext ();
					final PersistentCollection persistentCollection = collectionType.wrap (source, unsavedCol);
					final PersistentCollection resultCol = (PersistentCollection) resultValues[i];
					final Serializable currentKey = resultCol.getKey ();
					persistenceContext.addInitializedCollection (colPersister, persistentCollection, currentKey);
					values[i] = persistentCollection;
				}

				persister.setPropertyValues (object, values, entityMode);
			}
		}

		TypeHelper.deepCopy (values, types, persister.getPropertyUpdateability (), values, source);
		final Object version = Versioning.getVersion (values, persister);

		// lazyPropertiesAreUnfetched: Will be ignored, using the existing Entry
		// instead
		source.getPersistenceContext ().addEntity (object, status, values, key, version, LockMode.NONE, true, persister, false, true);
		persister.afterReassociate (object, source);
	}


	/** {@inheritDoc} */
	@Override
	public void onPostDelete (final PostDeleteEvent event)
	{
		final SessionImplementor source = event.getSession ();
		final EntityPersister per = event.getPersister ();
		final Session session = source.getFactory ().openSession ();
		Transaction t = null;
		try
		{
			t = session.beginTransaction ();
			final Object deletedObject = event.getEntity ();
			if (deletedObject instanceof OWLObject)
			{
				final Type [] types = per.getPropertyTypes ();
				final String [] names = per.getPropertyNames ();

				for (int i = 0; i < names.length; i++)
				{
					if (types[i] instanceof ManyToOneType)
					{
						final Object value = per.getPropertyValue (deletedObject, names[i], EntityMode.POJO);
						this.deleteReference (session, source, value);
					}
					else if (types[i] instanceof CollectionType)
					{
						final Object values = per.getPropertyValue (deletedObject, names[i], EntityMode.POJO);
						if (values instanceof PersistentCollection)
						{
							final PersistentCollection pc = (PersistentCollection) values;
							pc.forceInitialization ();
							for (final Object value: (Collection<?>) values)
								this.deleteReference (session, source, value);
						}
					}
				}
			}
			t.commit ();
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


	/** {@inheritDoc} */
	@Override
	public void onPostInsert (final PostInsertEvent event)
	{
		final SessionImplementor source = event.getSession ();
		final EntityPersister per = event.getPersister ();

		final Session session = source.getFactory ().openSession ();
		Transaction t = null;
		try
		{
			t = session.beginTransaction ();
			final Object insObject = event.getEntity ();
			if (insObject instanceof OWLObject)
			{
				// Set hash code when insert new owlobject
				final Serializable keyOfInsObject = source.getContextEntityIdentifier (insObject);
				final AnnotatedOWLObject annotatedObj = AnnotatedOWLObject.load (session, keyOfInsObject);
				annotatedObj.setHashCode (Long.valueOf (OWLObjectIdGenerator.getHashCode (insObject)));
				session.update (annotatedObj);

				final Type [] types = per.getPropertyTypes ();
				final String [] names = per.getPropertyNames ();

				for (int i = 0; i < names.length; i++)
				{
					if (types[i] instanceof ManyToOneType)
					{
						final Object value = per.getPropertyValue (insObject, names[i], EntityMode.POJO);
						this.addReference (session, source, value);
					}
					else if (types[i] instanceof CollectionType)
					{
						final Object values = per.getPropertyValue (insObject, names[i], EntityMode.POJO);
						if (values instanceof PersistentCollection)
						{
							for (final Object value: (Collection<?>) values)
								this.addReference (session, source, value);
						}
					}
				}
			}
			t.commit ();
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
	 * Increases the reference to the given value object.
	 * 
	 * @param session The Hibernate session
	 * @param source The Hibernate event session implementor
	 * @param value The added object
	 */
	protected void addReference (final Session session, final SessionImplementor source, final Object value)
	{
		if (!(value instanceof OWLObject))
			return;

		final Serializable key = source.getContextEntityIdentifier (value);
		final AnnotatedOWLObject obj = AnnotatedOWLObject.load (session, key);
		obj.increaseRefC ();
		session.update (obj);
	}


	/**
	 * Deletes the given value object if it is no longer referenced.
	 * 
	 * @param session The Hibernate session
	 * @param source The Hibernate event session implementor
	 * @param value The object to delete
	 */
	protected void deleteReference (final Session session, final SessionImplementor source, final Object value)
	{
		if (!(value instanceof OWLObject))
		{
			// 'Normal' delete
			session.delete (value);
			return;
		}

		final Serializable key = this.getID (source, (OWLObject) value);
		if (key == null)
			return;

		final AnnotatedOWLObject obj = AnnotatedOWLObject.load (session, key);
		// Decrease the reference counter
		obj.decreaseRefC ();
		session.update (obj);

		// Is the value still referenced?
		if (obj.isReferenced ())
			return;

		// The value is no longer referenced, delete it
		final PersistenceContext persistenceContext = source.getPersistenceContext ();
		final Object entity = persistenceContext.unproxyAndReassociate (value);

		final EntityEntry entityEntry = persistenceContext.getEntry (entity);

		if (entityEntry != null)
		{
			if (!entityEntry.getStatus ().equals (Status.DELETED))
				session.load (value, key);

			persistenceContext.setEntryStatus (entityEntry, Status.DELETED);

			session.delete (value);
		}
	}


	/**
	 * Get the Id of the OWLObject in the database.
	 * 
	 * @param source Hibernate source object
	 * @param object The OWLObject
	 * @return The ID of the object in the database
	 */
	private Long getID (final SessionImplementor source, final OWLObject object)
	{
		return HibernateUtil.executeTransaction (source.getFactory (), new HibernateWrapper<Long> ()
		{
			@Override
			public Long doInHibernate (final Session session)
			{
				return AnnotatedOWLObject.getID (session, object);
			}
		});
	}
}