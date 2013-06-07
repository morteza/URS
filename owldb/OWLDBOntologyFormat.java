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

package owldb;

import org.semanticweb.owlapi.model.*;


/**
 * The format of the OWLDBOntology.
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 */
public class OWLDBOntologyFormat extends OWLOntologyFormat
{
	/** {@inheritDoc} */
	@Override
	public String toString ()
	{
		return "OWLDBFORMAT";
	}
}
