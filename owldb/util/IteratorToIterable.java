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


/**
 * Allows to use an iterator as an iterable (e.g. in a for-loop).
 * 
 * @param <T> The type of the iterator
 * 
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 */
public class IteratorToIterable<T> implements Iterable<T>
{
	private Iterator<T>	iterator;


	/**
	 * Just for convenience.
	 * 
	 * @param <T> The type of the iterator
	 * @param iterator The iterator to convert
	 * @return The wrapped iterator
	 */
	public static <T> IteratorToIterable<T> makeIterable (final Iterator<T> iterator)
	{
		return new IteratorToIterable<T> (iterator);
	}


	/**
	 * Constructor.
	 * 
	 * @param iterator The iterator to convert
	 */
	public IteratorToIterable (final Iterator<T> iterator)
	{
		this.iterator = iterator;
	}


	/** {@inheritDoc} */
	@Override
	public Iterator<T> iterator ()
	{
		return this.iterator;
	}
}
