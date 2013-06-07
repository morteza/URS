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

import java.io.*;
import org.hibernate.engine.*;
import org.hibernate.id.*;
import org.semanticweb.owlapi.model.*;


/**
 * This class is used to generate an object ID for an OWLAPI object. Because
 * hash collisions occured while using simple hashing, it uses a 64 Bit hash
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 */
public class OWLObjectIdGenerator implements IdentifierGenerator
{
	/** 2 to the power of 32. */
	public static final long	TWO_POW_32	= 0x100000000L;


	/** {@inheritDoc} */
	@Override
	public Serializable generate (final SessionImplementor session, final Object object)
	{
		if (object instanceof OWLObject)
			return Long.valueOf (hash64 (object));
		return Integer.valueOf (object.hashCode ());
	}


	/**
	 * This method hashes an object by prepending the hash code of the class name
	 * to the normal hash code resulting in a 64 bit hash code.
	 * 
	 * @param object The object we wish to hash
	 * @return 64 bit hash code
	 */
	public static long hash64 (final Object object)
	{
		final long hash = object.hashCode ();
		// Convert negative integer to unsigned by adding 2^32
		final long objectHash = hash < 0 ? hash + TWO_POW_32 : hash;
		return ((long) object.getClass ().getName ().hashCode () << 32) + objectHash;
	}


	/**
	 * This method hashes an {@link OWLObject} by prepending the hash code of the
	 * class name to the normal hash code resulting in a 64 bit hash code. If the
	 * input object is not {@link OWLObject} return normal hash code
	 * 
	 * @param object The object we wish to hash
	 * @return The hash code
	 */
	public static long getHashCode (final Object object)
	{
		if (object instanceof OWLObject)
			return hash64 (object);
		return object.hashCode ();
	}
}
