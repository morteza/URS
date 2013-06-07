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

import org.hibernate.*;


/**
 * Wraps a Hibernate transaction.
 * 
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 * 
 * @param <T> The type of the result value
 */
public interface HibernateWrapper<T>
{
	/**
	 * Implement execute Hibernate calls. Does not need to care about activating
	 * or closing the Session, or handling transactions.
	 * 
	 * @param session The current Hibernate session
	 * @return A retrieved object
	 */
	T doInHibernate (final Session session);
}
