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

import owldb.util.instantiator.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;
import org.hibernate.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.*;
import uk.ac.manchester.cs.owl.owlapi.*;


/**
 * This class is used to instantiate OWLAPI objects with default values.
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 */
public class OWLDBInterceptor extends EmptyInterceptor
{
	private final static Logger											LOGGER						= Logger.getLogger ("services.pescado.knowledgebaseaccessservice");
	private final static long												serialVersionUID	= -1958608400545104086L;
	private final static Method []									FACTORY_METHODS		= OWLDataFactory.class.getMethods ();
	private final static Map<String, IInstantiator>	INSTANTIATORS			= new HashMap<String, IInstantiator> ();

	static
	{
		INSTANTIATORS.put (OWLOntologyImpl.class.getCanonicalName (), new OWLOntologyInstantiator ());
		INSTANTIATORS.put (OWLImportsDeclarationImpl.class.getCanonicalName (), new OWLImportsDeclarationInstantiator ());

		INSTANTIATORS.put (OWLClassImpl.class.getCanonicalName (), new OWLClassInstantiator ());
		INSTANTIATORS.put (OWLClassAssertionImpl.class.getCanonicalName (), new OWLClassAssertionInstantiator ());
		INSTANTIATORS.put (OWLSubClassOfAxiomImpl.class.getCanonicalName (), new OWLSubClassAxiomInstantiator ());
		INSTANTIATORS.put (OWLDisjointClassesAxiomImpl.class.getCanonicalName (), new OWLDisjointClassesAxiomInstantiator ());
		INSTANTIATORS.put (OWLDisjointUnionAxiomImpl.class.getCanonicalName (), new OWLDisjointUnionAxiomInstantiator ());
		INSTANTIATORS.put (OWLEquivalentClassesAxiomImpl.class.getCanonicalName (), new OWLEquivalentClassesAxiomInstantiator ());
		INSTANTIATORS.put (OWLHasKeyAxiomImpl.class.getCanonicalName (), new OWLHasKeyAxiomInstantiator ());

		INSTANTIATORS.put (OWLSubPropertyChainAxiomImpl.class.getCanonicalName (), new OWLSubPropertyChainAxiomInstantiator ());

		INSTANTIATORS.put (OWLObjectPropertyImpl.class.getCanonicalName (), new OWLObjectPropertyInstantiator ());
		INSTANTIATORS.put (OWLObjectPropertyAssertionAxiomImpl.class.getCanonicalName (), new OWLObjectPropertyAssertionAxiomInstantiator ());
		INSTANTIATORS.put (OWLSubObjectPropertyOfAxiomImpl.class.getCanonicalName (), new OWLSubObjectPropertyOfAxiomInstantiator ());
		INSTANTIATORS.put (OWLEquivalentObjectPropertiesAxiomImpl.class.getCanonicalName (), new OWLEquivalentObjectPropertiesAxiomInstantiator ());
		INSTANTIATORS.put (OWLInverseObjectPropertiesAxiomImpl.class.getCanonicalName (), new OWLInverseObjectPropertiesInstantiator ());
		INSTANTIATORS.put (OWLObjectMaxCardinalityImpl.class.getCanonicalName (), new OWLObjectMaxCardinalityRestrictionInstantiator ());
		INSTANTIATORS.put (OWLObjectMinCardinalityImpl.class.getCanonicalName (), new OWLObjectMinCardinalityRestrictionInstantiator ());
		INSTANTIATORS.put (OWLObjectExactCardinalityImpl.class.getCanonicalName (), new OWLObjectExactCardinalityRestrictionInstantiator ());
		INSTANTIATORS.put (OWLObjectPropertyRangeAxiomImpl.class.getCanonicalName (), new OWLObjectPropertyRangeAxiomInstantiator ());
		INSTANTIATORS.put (OWLObjectPropertyDomainAxiomImpl.class.getCanonicalName (), new OWLObjectPropertyDomainAxiomInstantiator ());
		INSTANTIATORS.put (OWLNegativeObjectPropertyAssertionAxiomImpl.class.getCanonicalName (), new OWLNegativeObjectPropertyAssertionAxiomInstantiator ());
		INSTANTIATORS.put (OWLAsymmetricObjectPropertyAxiomImpl.class.getCanonicalName (), new OWLAsymmetricObjectPropertyAxiomInstantiator ());
		INSTANTIATORS.put (OWLFunctionalObjectPropertyAxiomImpl.class.getCanonicalName (), new OWLFunctionalObjectPropertyAxiomInstantiator ());
		INSTANTIATORS.put (OWLInverseFunctionalObjectPropertyAxiomImpl.class.getCanonicalName (), new OWLInverseFunctionalObjectPropertyAxiomInstantiator ());
		INSTANTIATORS.put (OWLIrreflexiveObjectPropertyAxiomImpl.class.getCanonicalName (), new OWLIrreflexiveObjectPropertyAxiomInstantiator ());
		INSTANTIATORS.put (OWLReflexiveObjectPropertyAxiomImpl.class.getCanonicalName (), new OWLReflexiveObjectPropertyAxiomInstantiator ());
		INSTANTIATORS.put (OWLSymmetricObjectPropertyAxiomImpl.class.getCanonicalName (), new OWLSymmetricObjectPropertyAxiomInstantiator ());
		INSTANTIATORS.put (OWLTransitiveObjectPropertyAxiomImpl.class.getCanonicalName (), new OWLTransitiveObjectPropertyAxiomInstantiator ());
		INSTANTIATORS.put (OWLDisjointObjectPropertiesAxiomImpl.class.getCanonicalName (), new OWLDisjointObjectPropertiesAxiomInstantiator ());
		INSTANTIATORS.put (OWLEquivalentDataPropertiesAxiomImpl.class.getCanonicalName (), new OWLEquivalentDataPropertiesAxiomInstantiator ());

		INSTANTIATORS.put (OWLDataPropertyImpl.class.getCanonicalName (), new OWLDataPropertyInstantiator ());
		INSTANTIATORS.put (OWLDataPropertyAssertionAxiomImpl.class.getCanonicalName (), new OWLDataPropertyAssertionAxiomInstantiator ());
		INSTANTIATORS.put (OWLSubDataPropertyOfAxiomImpl.class.getCanonicalName (), new OWLSubDataPropertyOfAxiomInstantiator ());
		INSTANTIATORS.put (OWLDataMaxCardinalityImpl.class.getCanonicalName (), new OWLMaxCardinalityRestrictionInstantiator ());
		INSTANTIATORS.put (OWLDataMinCardinalityImpl.class.getCanonicalName (), new OWLDataMinCardinalityRestrictionInstantiator ());
		INSTANTIATORS.put (OWLDataExactCardinalityImpl.class.getCanonicalName (), new OWLDataExactCardinalityRestrictionInstantiator ());
		INSTANTIATORS.put (OWLDataPropertyRangeAxiomImpl.class.getCanonicalName (), new OWLDataPropertyRangeAxiomInstantiator ());
		INSTANTIATORS.put (OWLDataPropertyDomainAxiomImpl.class.getCanonicalName (), new OWLDataPropertyDomainAxiomInstantiator ());
		INSTANTIATORS.put (OWLNegativeDataPropertyAssertionImplAxiom.class.getCanonicalName (), new OWLNegativeDataPropertyAssertionAxiomInstantiator ());
		INSTANTIATORS.put (OWLFunctionalDataPropertyAxiomImpl.class.getCanonicalName (), new OWLFunctionalDataPropertyAxiomInstantiator ());
		INSTANTIATORS.put (OWLDisjointDataPropertiesAxiomImpl.class.getCanonicalName (), new OWLDisjointDataPropertiesAxiomInstantiator ());

		INSTANTIATORS.put (OWLDatatypeImpl.class.getCanonicalName (), new OWLDatatypeInstantiator ());
		INSTANTIATORS.put (OWLDatatypeDefinitionAxiomImpl.class.getCanonicalName (), new OWLDatatypeDefinitionAxiomInstantiator ());

		INSTANTIATORS.put (OWLFacet.class.getCanonicalName (), new OWLFacetInstantiator ());
		INSTANTIATORS.put (OWLFacetRestrictionImpl.class.getCanonicalName (), new OWLFacetRestrictionInstantiator ());
		INSTANTIATORS.put (OWLLiteralImpl.class.getCanonicalName (), new OWLLiteralInstantiator ());

		INSTANTIATORS.put (OWLObjectUnionOfImpl.class.getCanonicalName (), new OWLObjectUnionOfInstantiator ());
		INSTANTIATORS.put (OWLObjectIntersectionOfImpl.class.getCanonicalName (), new OWLObjectIntersectionOfInstantiator ());
		INSTANTIATORS.put (OWLObjectOneOfImpl.class.getCanonicalName (), new OWLObjectOneOfInstantiator ());
		INSTANTIATORS.put (OWLObjectHasSelfImpl.class.getCanonicalName (), new OWLObjectHasSelfInstantiator ());
		INSTANTIATORS.put (OWLDataOneOfImpl.class.getCanonicalName (), new OWLDataOneOfInstantiator ());
		INSTANTIATORS.put (OWLDataAllValuesFromImpl.class.getCanonicalName (), new OWLDataAllValuesFromInstantiator ());
		INSTANTIATORS.put (OWLDataHasValueImpl.class.getCanonicalName (), new OWLDataHasValueInstantiator ());
		INSTANTIATORS.put (OWLDataComplementOfImpl.class.getCanonicalName (), new OWLDataComplementOfInstantiator ());

		INSTANTIATORS.put (OWLObjectSomeValuesFromImpl.class.getCanonicalName (), new OWLObjectSomeValuesFromInstantiator ());
		INSTANTIATORS.put (OWLObjectAllValuesFromImpl.class.getCanonicalName (), new OWLObjectAllValuesFromInstantiator ());
		INSTANTIATORS.put (OWLObjectInverseOfImpl.class.getCanonicalName (), new OWLObjectInverseOfInstantiator ());
		INSTANTIATORS.put (OWLDataSomeValuesFromImpl.class.getCanonicalName (), new OWLDataSomeValuesFromInstantiator ());
		INSTANTIATORS.put (OWLDatatypeRestrictionImpl.class.getCanonicalName (), new OWLDatatypeRestrictionInstantiator ());
		INSTANTIATORS.put (OWLObjectComplementOfImpl.class.getCanonicalName (), new OWLObjectComplementOfInstantiator ());
		INSTANTIATORS.put (OWLNamedIndividualImpl.class.getCanonicalName (), new OWLNamedIndividualInstantiator ());
		INSTANTIATORS.put (OWLAnonymousIndividualImpl.class.getCanonicalName (), new OWLAnonymousIndividualInstantiator ());
		INSTANTIATORS.put (OWLSameIndividualAxiomImpl.class.getCanonicalName (), new OWLSameIndividualAxiomInstantiator ());
		INSTANTIATORS.put (OWLDifferentIndividualsAxiomImpl.class.getCanonicalName (), new OWLDifferentIndividualsInstantiator ());
		INSTANTIATORS.put (OWLObjectHasValueImpl.class.getCanonicalName (), new OWLObjectHasValueInstantiator ());

		INSTANTIATORS.put (OWLDeclarationAxiomImpl.class.getCanonicalName (), new OWLDeclarationAxiomInstantiator ());

		// Annotations
		INSTANTIATORS.put (OWLAnnotationPropertyImpl.class.getCanonicalName (), new OWLAnnotationPropertyInstantiator ());
		INSTANTIATORS.put (OWLAnnotationAssertionAxiomImpl.class.getCanonicalName (), new OWLAnnotationAssertionAxiomInstantiator ());
		INSTANTIATORS.put (OWLAnnotationPropertyDomainAxiomImpl.class.getCanonicalName (), new OWLAnnotationPropertyDomainAxiomInstantiator ());
		INSTANTIATORS.put (OWLAnnotationPropertyRangeAxiomImpl.class.getCanonicalName (), new OWLAnnotationPropertyRangeAxiomInstantiator ());
		INSTANTIATORS.put (OWLSubAnnotationPropertyOfAxiomImpl.class.getCanonicalName (), new OWLSubAnnotationPropertyOfAxiomInstantiator ());

		// IRI
		INSTANTIATORS.put ("org.semanticweb.owlapi.model.IRI$IRIImpl", new IRIInstantiator ());

		// SWRL
		INSTANTIATORS.put (SWRLVariableImpl.class.getCanonicalName (), new SWRLVariableInstantiator ());
		INSTANTIATORS.put (SWRLRuleImpl.class.getCanonicalName (), new SWRLRuleInstantiator ());
		INSTANTIATORS.put (SWRLClassAtomImpl.class.getCanonicalName (), new SWRLClassAtomInstantiator ());
		INSTANTIATORS.put (SWRLIndividualArgumentImpl.class.getCanonicalName (), new SWRLIndividualArgumentInstantiator ());

		INSTANTIATORS.put (SWRLLiteralArgumentImpl.class.getCanonicalName (), new SWRLLiteralArgumentInstantiator ());
		INSTANTIATORS.put (SWRLDataPropertyAtomImpl.class.getCanonicalName (), new SWRLDataPropertyAtomInstantiator ());
		INSTANTIATORS.put (SWRLDifferentIndividualsAtomImpl.class.getCanonicalName (), new SWRLDifferentIndividualsAtomInstantiator ());
		INSTANTIATORS.put (SWRLObjectPropertyAtomImpl.class.getCanonicalName (), new SWRLObjectPropertyAtomInstantiator ());
		INSTANTIATORS.put (SWRLSameIndividualAtomImpl.class.getCanonicalName (), new SWRLSameIndividualAtomInstantiator ());
		INSTANTIATORS.put (SWRLBuiltInAtomImpl.class.getCanonicalName (), new SWRLBuiltInAtomInstantiator ());
		INSTANTIATORS.put (SWRLDataRangeAtomImpl.class.getCanonicalName (), new SWRLDataRangeAtomInstantiator ());
	}

	final OWLOntologyManager												ontologyManager;


	/**
	 * The constructor.
	 * 
	 * @param ontologyManager The ontology manager to use
	 */
	public OWLDBInterceptor (final OWLOntologyManager ontologyManager)
	{
		this.ontologyManager = ontologyManager;
	}


	/**
	 * Get all instantiators.
	 * 
	 * @return All instantiators
	 */
	public static Map<String, IInstantiator> getInstantiators ()
	{
		return new HashMap<String, IInstantiator> (INSTANTIATORS);
	}


	/** {@inheritDoc} */
	@Override
	public Object instantiate (final String entityName, final EntityMode entityMode, final Serializable id)
	{
		if (entityMode != EntityMode.POJO)
			return null;

		try
		{
			final IRI iri = IRI.create (new StringBuilder ("jdbc:OWLDB://").append (entityName).append ('/').append (id).toString ());

			final IInstantiator instantiator = INSTANTIATORS.get (entityName);
			OWLDataFactory nullFactory = null;
			if (instantiator != null)
				return instantiator.instantiate(nullFactory, entityName, iri, id);
				//DEPRICATED: return instantiator.instantiate (null, entityName, iri, id);

			if (!entityName.startsWith ("uk.ac.manchester.cs.owl."))
				return null;

			final Class<?> clazz = Class.forName (entityName);
			String clazzName = clazz.getSimpleName ();
			if (clazzName.endsWith ("Impl"))
				clazzName = clazzName.replaceAll ("Impl", "");

			for (final Method m: FACTORY_METHODS)
			{
				if (!m.getName ().endsWith (clazzName))
					continue;

				final Class<?> [] paramTypes = m.getParameterTypes ();
				final Object [] args = new Object [paramTypes.length];
				for (int i = 0; i < args.length; i++)
				{
					if (Set.class.isAssignableFrom (paramTypes[i]))
						args[i] = new HashSet<Object> ();
					else if (List.class.isAssignableFrom (paramTypes[i]))
						args[i] = new ArrayList<Object> ();
					else if (paramTypes[i].isArray ())
					{
						final Class<?> type = paramTypes[i].getComponentType ();
						args[i] = Array.newInstance (type, 0);
					}
					else
						args[i] = null;
				}

				try
				{
					return m.invoke (this.ontologyManager.getOWLDataFactory(), args);
				}
				catch (final Exception ex)
				{
					LOGGER.log (Level.SEVERE, "Error while instantiating: " + entityName, ex);
				}
			}
		}
		catch (final ClassNotFoundException ex)
		{
			LOGGER.log (Level.SEVERE, "Error while instantiating: " + entityName, ex);
		}
		return null;
	}
}
