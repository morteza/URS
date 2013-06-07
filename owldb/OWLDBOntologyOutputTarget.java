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

import java.io.*;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;


/**
 * An Target for the OWLDBOntology.
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 */
public class OWLDBOntologyOutputTarget implements OWLOntologyDocumentTarget
{
	final IRI	iri;


	/**
	 * An Target for the OWLDBOntology.
	 * 
	 * @param iri The IRI to output to.
	 */
	public OWLDBOntologyOutputTarget (final IRI iri)
	{
		this.iri = iri;
	}


	/** {@inheritDoc} */
	@Override
	public OutputStream getOutputStream ()
	{
		return null;
	}


	/** {@inheritDoc} */
	@Override
	public IRI getDocumentIRI ()
	{
		return this.iri;
	}


	/** {@inheritDoc} */
	@Override
	public Writer getWriter ()
	{
		return null;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isOutputStreamAvailable ()
	{
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isDocumentIRIAvailable ()
	{
		return true;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isWriterAvailable ()
	{
		return false;
	}
}
