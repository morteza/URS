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
import java.sql.*;
import org.hibernate.type.*;
import org.hibernate.usertype.*;
import org.semanticweb.owlapi.model.*;


/**
 * This class is used to store NodeIDs in VARCHAR type fields.
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 */
public class NodeIDStringType implements UserType
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
		return x != null && x instanceof NodeID && ((NodeID) x).equals (y);
	}


	/** {@inheritDoc} */
	@SuppressWarnings("deprecation")
	@Override
	public Object nullSafeGet (final java.sql.ResultSet rs, final String [] names, final Object owner) throws SQLException
	{
		final String value = StandardBasicTypes.STRING.nullSafeGet (rs, names[0]);
		return value == null ? null : NodeID.getNodeID (value);
	}


	/** {@inheritDoc} */
	@SuppressWarnings("deprecation")
	@Override
	public void nullSafeSet (final PreparedStatement pstmt, final Object value, final int index) throws SQLException
	{
		final String text = value == null ? "" : value instanceof NodeID ? ((NodeID) value).getID () : value.toString ();
		StandardBasicTypes.STRING.nullSafeSet (pstmt, text, index);
	}


	/** {@inheritDoc} */
	@Override
	public Class<?> returnedClass ()
	{
		return NodeID.class;
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
		return value instanceof NodeID ? ((NodeID) value).getID () : null;
	}


	/** {@inheritDoc} */
	@Override
	public Object assemble (final Serializable cached, final Object owner)
	{
		return cached instanceof String ? NodeID.getNodeID((String) cached) : null;
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
		return obj instanceof NodeID ? NodeID.getNodeID (((NodeID) obj).getID ()) : null;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isMutable ()
	{
		return true;
	}
}
