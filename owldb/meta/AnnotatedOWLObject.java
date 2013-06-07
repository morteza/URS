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

package owldb.meta;

import owldb.util.*;
import java.io.*;
import java.util.*;
import org.hibernate.*;
import org.semanticweb.owlapi.model.*;


/**
 * This Class stores the additional information for {@link OWLObject} like id,
 * reference count and hash Code.
 * 
 * @author Yongchun Xu (FZI)
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 */
public class AnnotatedOWLObject
{
	private Long	id;
	private long	refC;
	private Long	hashCode;


	/**
	 * Get the ID.
	 * 
	 * @return The ID
	 */
	public Long getId ()
	{
		return this.id;
	}


	/**
	 * Set the ID.
	 * 
	 * @param id The id to set
	 */
	public void setId (final Long id)
	{
		this.id = id;
	}


	/**
	 * Get the reference count.
	 * 
	 * @return The refCount
	 */
	public long getRefC ()
	{
		return this.refC;
	}


	/**
	 * Set the reference count.
	 * 
	 * @param refCount the refCount to set
	 */
	public void setRefC (final Long refCount)
	{
		this.refC = refCount == null ? 0 : refCount.longValue ();
	}


	/**
	 * Increases the reference counter.
	 */
	public void increaseRefC ()
	{
		this.refC++;
	}


	/**
	 * Decreases the reference counter.
	 */
	public void decreaseRefC ()
	{
		this.refC = Math.max (0, this.refC - 1);
	}


	/**
	 * Returns true if the object is referenced (reference counter greater zero).
	 * 
	 * @return True if referenced
	 */
	public boolean isReferenced ()
	{
		return this.refC > 0;
	}


	/**
	 * Set the Hash Code.
	 * 
	 * @param hashCode The hash code
	 */
	public void setHashCode (final Long hashCode)
	{
		this.hashCode = hashCode;
	}


	/**
	 * Get the hash code.
	 * 
	 * @return The hash code
	 */
	public Long getHashCode ()
	{
		return this.hashCode;
	}


	/**
	 * Load an AnnotatedOWLObject.
	 * 
	 * @param session The Hibernate session.
	 * @param key The key of the object
	 * @return The loaded object or null
	 */
	public static AnnotatedOWLObject load (final Session session, final Serializable key)
	{
		return (AnnotatedOWLObject) session.load (AnnotatedOWLObject.class, key);
	}


	/**
	 * Load an AnnotatedOWLObject.
	 * 
	 * @param session The Hibernate session.
	 * @param object The OWLObject
	 * @return The loaded object or null
	 */
	public static AnnotatedOWLObject load (final Session session, final OWLObject object)
	{
		final Long key = getID (session, object);
		return key == null ? null : (AnnotatedOWLObject) session.load (AnnotatedOWLObject.class, key);
	}


	/**
	 * Get the Id of the OWLObject in the database.
	 * 
	 * @param session The Hibernate session
	 * @param object The OWLObject
	 * @return The ID of the object in the database or null if not found
	 */
	public static Long getID (final Session session, final OWLObject object)
	{
		if (object == null)
			return null;

		final long hashCode = OWLObjectIdGenerator.getHashCode (object);
		for (final Long id: getByHashCode (session, hashCode))
		{
			final OWLObject obj = (OWLObject) session.get (object.getClass (), id);
			if (obj != null && obj.equals (object))
				return id;
		}

		return null;
	}


	/**
	 * Get the OWLObject from the database.
	 * 
	 * @param session The Hibernate session
	 * @param object The OWLObject
	 * @return The OWLObject from database
	 */
	public static OWLObject getOWLObjectFromDB (final Session session, final Object object)
	{
		if (object == null)
			return null;

		final long hashCode = OWLObjectIdGenerator.getHashCode (object);
		for (final Long id: getByHashCode (session, hashCode))
		{
			final OWLObject obj = (OWLObject) session.get (object.getClass (), id);
			if (obj != null && obj.equals (object))
				return obj;
		}

		return null;
	}


	/**
	 * Get the IDs of all annotated OWL objects which match the given hash code.
	 * 
	 * @param session The Hibernate session
	 * @param hashCode The hash code
	 * @return The list with all matching IDs
	 */
	@SuppressWarnings("unchecked")
	private static Iterable<Long> getByHashCode (final Session session, final long hashCode)
	{
		final Query query = session.createQuery ("select obj.id from AnnotatedOWLObject obj where obj.hashCode=:hash");
		query.setCacheable (true);
		query.setLong ("hash", hashCode);
		return IteratorToIterable.makeIterable ((Iterator<Long>) query.iterate ());
	}
}
