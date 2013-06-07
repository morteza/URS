// Copyright (c) 2011. This source code is available under the terms of the GNU Lesser General Public License (LGPL)
// Author: Mario Volke <volke@derivo.de>
// derivo GmbH, James-Franck-Ring, 89081 Ulm 

package sparqldl;

import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import sparqldl.exceptions.QueryEngineException;
import sparqldl.exceptions.QueryParserException;
import sparqldl.impl.QueryEngineImpl;


/**
 * The query engine that executes a query and generates the appropriate result set.
 * 
 * @author Mario Volke
 */
public abstract class QueryEngine 
{	
	/**
	 * Factory method to create a QueryEngine instance.
	 * 
	 * @param manager An OWLOntologyManager instance of OWLAPI v3
	 * @param reasoner An OWLReasoner instance.
	 * @return an instance of QueryEngine
	 */
	public static QueryEngine create(OWLOntologyManager manager, OWLReasoner reasoner)
	{
		return new QueryEngineImpl(manager, reasoner);
	}
	
	/**
	 * Factory method to create a QueryEngine instance.
	 * 
	 * @param manager An OWLOntologyManager instance of OWLAPI v3
	 * @param reasoner An OWLReasoner instance.
	 * @param strictMode If strict mode is enabled the query engine will throw a QueryEngineException if data types withing the query are not correct (e.g. Class(URI_OF_AN_INDIVIDUAL))
	 * @return an instance of QueryEngine
	 */
	public static QueryEngine create(OWLOntologyManager manager, OWLReasoner reasoner, boolean strict)
	{
		return new QueryEngineImpl(manager, reasoner, strict);
	}
	
	/**
	 * Execute a sparql-dl query and generate the result set.
	 * 
	 * @param query
	 * @return The query result set.
	 */
	public abstract QueryResult execute(Query query)
		throws QueryEngineException, QueryParserException;

	/**
	 * Execute a sparql-dl string query and generate the result set.
	 * 
	 * @param query of type String
	 * @return The query result set.
	 * @throws QueryEngineException
	 * @throws QueryParserException 
	 */
	public abstract QueryResult execute(String query)
		throws QueryEngineException, QueryParserException;
}
