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
import org.hibernate.usertype.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.*;


/**
 * A UserType for OWLFacet.
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 */
public class OWLFacetEnumType implements UserType
{

	private static final int []	SQL_TYPES	=
																				{
																					Types.VARCHAR
																				};


	/** {@inheritDoc} */
	@Override
	public int [] sqlTypes ()
	{
		return SQL_TYPES.clone ();
	}


	/** {@inheritDoc} */
	@Override
	public Class<?> returnedClass ()
	{
		return OWLFacet.class;
	}


	/** {@inheritDoc} */
	@Override
	public boolean equals (final Object x, final Object y)
	{
		return x == y;
	}


	/** {@inheritDoc} */
	@Override
	public Object deepCopy (final Object value)
	{
		return value;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isMutable ()
	{
		return false;
	}


	/** {@inheritDoc} */
	@SuppressWarnings("deprecation")
	@Override
	public Object nullSafeGet (final ResultSet resultSet, final String [] names, final Object owner) throws SQLException
	{
		final String iri = resultSet.getString (names[0]);
		return resultSet.wasNull () ? null : OWLFacet.getFacet (IRI.create (iri));
	}


	/** {@inheritDoc} */
	@SuppressWarnings("deprecation")
	@Override
	public void nullSafeSet (final PreparedStatement statement, final Object value, final int index) throws SQLException
	{
		if (value == null)
			statement.setNull (index, Types.VARCHAR);
		else
		{
			if (value instanceof OWLFacet)
			{
				final OWLFacet facet = (OWLFacet) value;
				statement.setString (index, facet.getIRI ().toString ());
			}
			else
				statement.setString (index, value.toString ());
		}
	}


	/** {@inheritDoc} */
	@Override
	public Serializable disassemble (final Object value)
	{
		return (Serializable) this.deepCopy (value);
	}


	/** {@inheritDoc} */
	@Override
	public Object assemble (final Serializable cached, final Object owner)
	{
		return this.deepCopy (cached);
	}


	/** {@inheritDoc} */
	@Override
	public Object replace (final Object original, final Object target, final Object owner)
	{
		return this.deepCopy (original);
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode (final Object x)
	{
		if (!(x instanceof OWLFacet))
			return x.hashCode ();

		final OWLFacet facet = (OWLFacet) x;
		return facet.getIRI ().toString ().hashCode ();
	}
}
