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
import java.net.*;
import java.sql.*;
import org.hibernate.type.*;
import org.hibernate.usertype.*;
import org.semanticweb.owlapi.model.*;


/**
 * This class is used to store IRIs in VARCHAR type fields.
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 */
public class IRIStringType implements UserType
{
	private static final int []	TYPES	=
																		{
																			java.sql.Types.VARCHAR
																		};


	/** {@inheritDoc} */
	@Override
	public int [] sqlTypes ()
	{
		return TYPES.clone ();
	}


	/** {@inheritDoc} */
	@Override
	public boolean equals (final Object x, final Object y)
	{
		return x != null && x instanceof IRI && ((IRI) x).equals (y);
	}


	/** {@inheritDoc} */
	@SuppressWarnings("deprecation")
	@Override
	public Object nullSafeGet (final java.sql.ResultSet rs, final String [] names, final Object owner) throws SQLException
	{
		final String value = StandardBasicTypes.STRING.nullSafeGet (rs, names[0]);
		return value == null ? null : IRI.create (value);
	}


	/** {@inheritDoc} */
	@SuppressWarnings("deprecation")
	@Override
	public void nullSafeSet (final PreparedStatement pstmt, final Object value, final int index) throws SQLException
	{
		final String text = value == null ? "" : value.toString ();
		StandardBasicTypes.STRING.nullSafeSet (pstmt, text, index);
	}


	/** {@inheritDoc} */
	@Override
	public Class<?> returnedClass ()
	{
		return IRI.class;
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode (final Object x)
	{
		return x.toString ().hashCode ();
	}


	/** {@inheritDoc} */
	@Override
	public Serializable disassemble (final Object value)
	{
		// Note: IRI does not implement Serializable, so we return an URI. The IRI
		// java doc states that they are fully compatible so this should be fine.
		return value instanceof IRI ? URI.create (((IRI) value).toURI ().toString ()) : null;
	}


	/** {@inheritDoc} */
	@Override
	public Object assemble (final Serializable cached, final Object owner)
	{
		// See the note in the disassemble method
		return cached instanceof URI ? IRI.create (((URI) cached).toString ()) : null;
	}


	/** {@inheritDoc} */
	@Override
	public Object replace (final Object original, final Object target, final Object owner)
	{
		return this.deepCopy (original);
	}


	/** {@inheritDoc} */
	@Override
	public Object deepCopy (final Object obj)
	{
		return obj instanceof IRI ? IRI.create (((IRI) obj).toString ()) : null;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isMutable ()
	{
		return true;
	}
}
