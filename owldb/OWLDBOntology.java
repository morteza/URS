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

import owldb.meta.*;
import owldb.util.*;
import java.util.*;

import org.hibernate.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import org.slf4j.*;
import uk.ac.manchester.cs.owl.owlapi.*;


/**
 * The Hibernate based implementation of the OWL API.
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 */
public class OWLDBOntology implements OWLMutableOntology
{
	private static final Map<AxiomType<? extends OWLAxiom>, Class<? extends OWLAxiom>>	AXIOM_TYPE_CLASSES;
	static
	{
		AXIOM_TYPE_CLASSES = new HashMap<AxiomType<? extends OWLAxiom>, Class<? extends OWLAxiom>> ();
		AXIOM_TYPE_CLASSES.put (AxiomType.SUBCLASS_OF, OWLSubClassOfAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.EQUIVALENT_CLASSES, OWLEquivalentClassesAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.DISJOINT_CLASSES, OWLDisjointClassesAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.CLASS_ASSERTION, OWLClassAssertionAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.SAME_INDIVIDUAL, OWLSameIndividualAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.DIFFERENT_INDIVIDUALS, OWLDifferentIndividualsAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.OBJECT_PROPERTY_ASSERTION, OWLObjectPropertyAssertionAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION, OWLNegativeObjectPropertyAssertionAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.DATA_PROPERTY_ASSERTION, OWLDataPropertyAssertionAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION, OWLNegativeDataPropertyAssertionAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.OBJECT_PROPERTY_DOMAIN, OWLObjectPropertyDomainAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.OBJECT_PROPERTY_RANGE, OWLObjectPropertyRangeAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.DISJOINT_OBJECT_PROPERTIES, OWLDisjointObjectPropertiesAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.SUB_OBJECT_PROPERTY, OWLSubObjectPropertyOfAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.EQUIVALENT_OBJECT_PROPERTIES, OWLEquivalentObjectPropertiesAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.INVERSE_OBJECT_PROPERTIES, OWLInverseObjectPropertiesAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.SUB_PROPERTY_CHAIN_OF, OWLSubPropertyChainOfAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.FUNCTIONAL_OBJECT_PROPERTY, OWLFunctionalObjectPropertyAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY, OWLInverseFunctionalObjectPropertyAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.SYMMETRIC_OBJECT_PROPERTY, OWLSymmetricObjectPropertyAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.ASYMMETRIC_OBJECT_PROPERTY, OWLAsymmetricObjectPropertyAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.TRANSITIVE_OBJECT_PROPERTY, OWLTransitiveObjectPropertyAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.REFLEXIVE_OBJECT_PROPERTY, OWLReflexiveObjectPropertyAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.IRREFLEXIVE_OBJECT_PROPERTY, OWLIrreflexiveObjectPropertyAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.DATA_PROPERTY_DOMAIN, OWLDataPropertyDomainAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.DATA_PROPERTY_RANGE, OWLDataPropertyRangeAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.DISJOINT_DATA_PROPERTIES, OWLDisjointDataPropertiesAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.SUB_DATA_PROPERTY, OWLSubDataPropertyOfAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.EQUIVALENT_DATA_PROPERTIES, OWLEquivalentDataPropertiesAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.FUNCTIONAL_DATA_PROPERTY, OWLFunctionalDataPropertyAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.DATATYPE_DEFINITION, OWLDatatypeDefinitionAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.DISJOINT_UNION, OWLDisjointUnionAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.DECLARATION, OWLDeclarationAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.SWRL_RULE, SWRLRule.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.ANNOTATION_ASSERTION, OWLAnnotationAssertionAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.SUB_ANNOTATION_PROPERTY_OF, OWLSubAnnotationPropertyOfAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.ANNOTATION_PROPERTY_DOMAIN, OWLAnnotationPropertyDomainAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.ANNOTATION_PROPERTY_RANGE, OWLAnnotationPropertyRangeAxiom.class);
		AXIOM_TYPE_CLASSES.put (AxiomType.HAS_KEY, OWLHasKeyAxiom.class);
	}

	final static Logger																																	LOGGER																= LoggerFactory.getLogger (OWLDBOntologyManager.class.getName ());

	private final static String																													CLASS_AXIOM_QUERY											= "select clsAx from OWLNaryClassAxiomImpl clsAx, OWLClassImpl cls where cls.id = :id and cls in elements(clsAx.classExpressions) ";
	private final static String																													SUB_CLASS_AXIOM_QUERY									= "select clsAx from OWLSubClassOfAxiomImpl clsAx, OWLClassImpl cls where cls.id = :id and (clsAx.subClass=cls OR clsAx.superClass=cls)";
	private final static String																													AXIOM_QUERY_STRING										= "select owlAx from org.semanticweb.owlapi.model.OWLAxiom owlAx";
	private final static String																													AXIOM_COUNT_QUERY_STRING							= "select count(*) from org.semanticweb.owlapi.model.OWLAxiom owlAx";
	private final static String																													AXIOMTYPE_COUNT_QUERY_STRING_PREFIX		= "select count(*) from ";
	private final static String																													AXIOMTYPE_COUNT_QUERY_STRING_SUFFIX		= " owlAx";
	private final static String																													EQUIVALENT_QUERY_STRING								= "select clsAx from OWLEquivalentClassesAxiomImpl clsAx, OWLClassImpl cls where cls.id = :id and cls in elements(clsAx.classExpressions)";
	private final static String																													DISJOINT_QUERY_STRING									= "select clsAx from OWLDisjointClassesAxiomImpl clsAx, OWLClassImpl cls where cls.id = :id and cls in elements(clsAx.classExpressions)";
	private final static String																													DIFFERENT_NAMED_INDI_QUERYSTRING			= "select indiAx from OWLDifferentIndividualsAxiomImpl indiAx, OWLNamedIndividualImpl indi where indi.id = :id and indi in elements(indiAx.individuals)";
	private final static String																													DIFFERENT_ANONYMOUS_INDI_QUERYSTRING	= "select indiAx from OWLDifferentIndividualsAxiomImpl indiAx, OWLAnonymousIndividualImpl indi where indi.id = :id and indi in elements(indiAx.individuals)";
	private final static String																													DISJOINT_CLASS_QUERY_STRING						= "select classAx from OWLDisjointClassesAxiomImpl classAx, OWLClassImpl cls where cls.id = :id and cls in elements(classAx.classExpressions)";
	private final static String																													DISJOINT_DP_QUERY_STRING							= "select propertyAx from OWLDisjointDataPropertiesAxiomImpl propertyAx, OWLDataPropertyImpl property where property.id = :id and property in elements(propertyAx.properties)";
	private final static String																													DISJOINT_OP_QUERY_STRING							= "select propertyAx from OWLDisjointObjectPropertiesAxiomImpl propertyAx, OWLObjectPropertyExpressionImpl property where property.id = :id and property in elements(propertyAx.properties)";
	private final static String																													EQUIVALENT_CLASS_QUERY_STRING					= "select classAx from OWLEquivalentClassesAxiomImpl classAx, OWLClassImpl cls where cls.id = :id and cls in elements(classAx.classExpressions)";
	private final static String																													EQUIVALENT_DP_QUERY_STRING						= "select propertyAx from OWLEquivalentDataPropertiesAxiomImpl propertyAx, OWLDataPropertyImpl property where property.id = :id and property in elements(propertyAx.properties)";
	private final static String																													EQUIVALENT_OP_QUERY_STRING						= "select propertyAx from OWLEquivalentObjectPropertiesAxiomImpl propertyAx, OWLObjectPropertyExpressionImpl property where property.id = :id and property in elements(propertyAx.properties)";
	private final static String																													QUERY_SUB_CLASS_STRING								= "select classAx from OWLSubClassOfAxiomImpl classAx where classAx.subClass not in (select cls FROM OWLClassImpl cls)";
	private final static String																													QUERY_EQ_CLASS_STRING									= "select classAx from OWLEquivalentClassesAxiomImpl classAx where not exists(select cls FROM OWLClassImpl cls  where cls in elements(classAx.classExpressions))";
	private final static String																													QUERY_DIS_CLASS_STRING								= "select classAx from OWLDisjointClassesAxiomImpl classAx where not exists(select cls FROM OWLClassImpl cls  where cls in elements(classAx.classExpressions))";
	private final static String																													INVERSE_OP_QUERY_STRING								= "select propertyAx from OWLInverseObjectPropertiesAxiomImpl propertyAx, OWLObjectPropertyExpressionImpl property where property.id = :id and property in elements(propertyAx.properties)";
	private final static String																													QUERY_NARY_OBJPROP_STRING							= "select propertyAx from OWLNaryPropertyAxiomImpl propertyAx, OWLObjectPropertyExpressionImpl property where property.id = :id and property in elements(propertyAx.properties)";
	private final static String																													QUERY_CHAIN_PROP_STRING								= "select propertyAx from OWLSubPropertyChainAxiomImpl propertyAx, OWLObjectPropertyExpressionImpl property where property.id = :id and (property in elements(propertyAx.propertyChain) OR property = propertyAx.superProperty)";
	private final static String																													QUERY_OBJ_SUBPROP_STRING							= "select propertyAx from OWLSubPropertyAxiomImpl propertyAx, OWLObjectPropertyExpressionImpl property where property.id = :id and (propertyAx.subProperty=property OR propertyAx.superProperty=property)";
	private final static String																													QUERY_NARY_DATAPROP_STRING						= "select propertyAx from OWLNaryPropertyAxiomImpl propertyAx, OWLDataPropertyImpl property where property.id = :id and property in elements(propertyAx.properties)";
	private final static String																													QUERY_DATA_SUBPROP_STRING							= "select propertyAx from OWLSubPropertyAxiomImpl propertyAx, OWLDataPropertyImpl property where property.id = :id and (propertyAx.subProperty=property OR propertyAx.superProperty=property)";
	private final static String																													QUERY_NARY_INDI_STRING								= "select indiAx from OWLNaryIndividualAxiomImpl indiAx, OWLNamedIndividualImpl indi where indi.id = :id and indi in elements(indiAx.individuals)";
	private final static String																													QUERY_INDI_REL_STRING									= "select indiAx from OWLIndividualRelationshipAxiomImpl indiAx, OWLNamedIndividualImpl indi where indi.id = :id and (indi = indiAx.subject OR indi = indiAx.object)";
	private final static String																													SAME_NAMED_INDI_QUERY_STRING					= "select indiAx from OWLSameIndividualAxiomImpl indiAx, OWLNamedIndividualImpl indi where indi.id = :id and indi in elements(indiAx.individuals)";
	private final static String																													SAME_ANONYMOUS_INDI_QUERY_STRING			= "select indiAx from OWLSameIndividualAxiomImpl indiAx, OWLAnonymousIndividualImpl indi where indi.id = :id and indi in elements(indiAx.individuals)";
	private final static String																													NARY_NAMED_INDIVIDUAL_QUERY						= "select indiAx from OWLNaryIndividualAxiomImpl indiAx, OWLNamedIndividualImpl indi where indi.id = :id and indi in elements(indiAx.individuals)";
	private final static String																													NARY_ANONYMOUS_INDIVIDUAL_QUERY				= "select indiAx from OWLNaryIndividualAxiomImpl indiAx, OWLAnonymousIndividualImpl indi where indi.id = :id and indi in elements(indiAx.individuals)";
	private final static String																													NARY_NAMED_INDIVIDUAL_REL_QUERY				= "select indiAx from OWLIndividualRelationshipAxiomImpl indiAx, OWLNamedIndividualImpl indi where indi.id = :id and (indi = indiAx.subject OR indi = indiAx.object)";
	private final static String																													NARY_ANONYMOUS_INDIVIDUAL_REL_QUERY		= "select indiAx from OWLIndividualRelationshipAxiomImpl indiAx, OWLAnonymousIndividualImpl indi where indi.id = :id and (indi = indiAx.subject OR indi = indiAx.object)";

	protected OWLDataFactory																														dataFactory;
	protected OWLOntologyManager																												ontologyManager;
	protected HibernateUtil																															db;
	protected OntologyIRI																																ontologyIRI;


	/**
	 * The constructor of the OWLDBOntology.
	 * 
	 * @param ontologyID The logical URI to use
	 * @param iri The physical URI that refers to a database
	 * @param ontologyManager The ontology manager
	 * @param props Properties used to configure Hibernate
	 */
	public OWLDBOntology (final OWLOntologyID ontologyID, final IRI iri, final OWLOntologyManager ontologyManager, final Properties props)
	{
		LOGGER.info ("Loading ontology: " + iri);
		this.ontologyManager = ontologyManager;
		this.dataFactory = ontologyManager.getOWLDataFactory ();

		this.db = new HibernateUtil (ontologyManager, props);

		this.ontologyIRI = this.db.execute (new HibernateWrapper<OntologyIRI> ()
		{
			@Override
			public OntologyIRI doInHibernate (final Session session)
			{
				if (ontologyID == null)
				{
					// Note: There is a conceptional problem here. The ontology is stored
					// with the IRI of the stored ontology but the access to the ontology
					// is always via the database connection string IRI. So we can't do a
					// comparison here. This means only 1 ontology is supported in the
					// database.
					return (OntologyIRI) session.createCriteria (OntologyIRI.class).uniqueResult ();
				}

				// Was created
				final OntologyIRI ontologyIRI = new OntologyIRI (ontologyID);
				session.save (ontologyIRI);
				return ontologyIRI;
			}
		});
		LOGGER.info ("Logical IRI is: " + (this.ontologyIRI == null ? "null" : this.ontologyIRI.getOntologyIRI ()));
	}


	/**
	 * Frees up the database connection and all involved resources. You can't use
	 * the object aferwards.
	 */
	public void destroyConnection ()
	{
		this.db.destroy ();
	}


	/** {@inheritDoc} */
	@Override
	public List<OWLOntologyChange> applyChange (final OWLOntologyChange change)
	{
		return this.applyChanges (Collections.singletonList (change));
	}


	/** {@inheritDoc} */
	@Override
	public List<OWLOntologyChange> applyChanges (final List<OWLOntologyChange> changes)
	{
		return this.db.execute (new HibernateWrapper<List<OWLOntologyChange>> ()
		{
			@Override
			public List<OWLOntologyChange> doInHibernate (final Session session)
			{
				final List<OWLOntologyChange> applied = new ArrayList<OWLOntologyChange> (changes);
				for (final OWLOntologyChange change: changes)
				{
					LOGGER.debug ("Applying change: " + change);
					if (change.isAxiomChange ())
					{
						final OWLAxiom ax = change.getAxiom ();
						final OWLAxiom persistedAxiom = (OWLAxiom) AnnotatedOWLObject.getOWLObjectFromDB (session, ax);

						// Store if not yet present
						if (change instanceof AddAxiom && persistedAxiom == null)
						{
							session.save (ax);
							LOGGER.debug ("Saved: " + ax);
							continue;
						}

						// Delete persistent entities
						if (change instanceof RemoveAxiom && persistedAxiom != null)
						{
							final AnnotatedOWLObject annoObject = AnnotatedOWLObject.load (session, ax);
							if (annoObject != null && annoObject.isReferenced ())
								throw new OWLOntologyChangeVetoException (change, "Can't delete object '" + annoObject + "' because it is still referenced.");

							session.delete (persistedAxiom);
							LOGGER.debug ("Deleted: " + persistedAxiom);
							continue;
						}
					}

					if (change instanceof SetOntologyID)
					{
						final SetOntologyID setID = (SetOntologyID) change;
						OWLDBOntology.this.ontologyIRI.setOntologyIRI (setID.getNewOntologyID ().getOntologyIRI ());
						session.saveOrUpdate (OWLDBOntology.this.ontologyIRI);
						continue;
					}

					applied.remove (change);
				}
				return applied;
			}
		});
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsAxiom (final OWLAxiom axiom)
	{
		return this.containsOWLObject (axiom);
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsAxiom (final OWLAxiom axiom, final boolean includeImportsClosure)
	{
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
		{
			if (owlOntology.containsAxiom (axiom))
				return true;
		}
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsAxiomIgnoreAnnotations (final OWLAxiom axiom)
	{
		if (axiom == null)
			return false;

		final OWLObject result = this.db.execute (new HibernateWrapper<OWLObject> ()
		{
			@Override
			public OWLObject doInHibernate (final Session session)
			{
				return AnnotatedOWLObject.getOWLObjectFromDB (session, axiom);
			}
		});

		return result != null && !(result instanceof OWLAnnotationAxiom);
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsAxiomIgnoreAnnotations (final OWLAxiom axiom, final boolean includeImportsClosure)
	{
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
		{
			if (owlOntology.containsAxiomIgnoreAnnotations (axiom))
				return true;
		}
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsEntityInSignature (final OWLEntity owlEntity)
	{
		return this.containsOWLObject (owlEntity);
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsEntityInSignature (final IRI entityIRI)
	{
		if (this.containsAnnotationPropertyInSignature (entityIRI))
			return true;
		if (this.containsDatatypeInSignature (entityIRI))
			return true;
		if (this.containsDataPropertyInSignature (entityIRI))
			return true;
		if (this.containsObjectPropertyInSignature (entityIRI))
			return true;
		if (this.containsClassInSignature (entityIRI))
			return true;
		if (this.containsIndividualInSignature (entityIRI))
			return true;

		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsEntityInSignature (final OWLEntity owlEntity, final boolean includeImportsClosure)
	{
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
		{
			if (owlOntology.containsEntityInSignature (owlEntity))
				return true;
		}
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsEntityInSignature (final IRI entityIRI, final boolean includeImportsClosure)
	{
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
		{
			if (owlOntology.containsEntityInSignature (entityIRI))
				return true;
		}
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsClassInSignature (final IRI owlClassIRI)
	{
		return owlClassIRI == null ? false : this.containsOWLObject (this.dataFactory.getOWLClass (owlClassIRI));
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsClassInSignature (final IRI owlClassIRI, final boolean includeImportsClosure)
	{
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
		{
			if (owlOntology.containsClassInSignature (owlClassIRI))
				return true;
		}
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsObjectPropertyInSignature (final IRI propIRI)
	{
		return propIRI == null ? false : this.containsOWLObject (this.dataFactory.getOWLObjectProperty (propIRI));
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsObjectPropertyInSignature (final IRI propIRI, final boolean includeImportsClosure)
	{
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
		{
			if (owlOntology.containsObjectPropertyInSignature (propIRI))
				return true;
		}
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsDataPropertyInSignature (final IRI propIRI)
	{
		return propIRI == null ? false : this.containsOWLObject (this.dataFactory.getOWLDataProperty (propIRI));
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsDataPropertyInSignature (final IRI propIRI, final boolean includeImportsClosure)
	{
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
		{
			if (owlOntology.containsDataPropertyInSignature (propIRI))
				return true;
		}
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsDatatypeInSignature (final IRI datatypeIRI)
	{
		return datatypeIRI == null ? false : this.containsOWLObject (this.dataFactory.getOWLDatatype (datatypeIRI));
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsDatatypeInSignature (final IRI datatypeIRI, final boolean includeImportsClosure)
	{
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
		{
			if (owlOntology.containsDatatypeInSignature (datatypeIRI))
				return true;
		}
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsIndividualInSignature (final IRI individualIRI)
	{
		return individualIRI == null ? false : this.containsOWLObject (this.dataFactory.getOWLNamedIndividual (individualIRI));
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsIndividualInSignature (final IRI individualIRI, final boolean includeImportsClosure)
	{
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
		{
			if (owlOntology.containsIndividualInSignature (individualIRI))
				return true;
		}
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsAnnotationPropertyInSignature (final IRI propIRI)
	{
		return propIRI == null ? false : this.containsOWLObject (this.dataFactory.getOWLAnnotationProperty (propIRI));
	}


	/** {@inheritDoc} */
	@Override
	public boolean containsAnnotationPropertyInSignature (final IRI propIRI, final boolean includeImportsClosure)
	{
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
		{
			if (owlOntology.containsAnnotationPropertyInSignature (propIRI))
				return true;
		}
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isDeclared (final OWLEntity owlEntity)
	{
		final Long key = this.getID (owlEntity);
		return key != null && this.db.exists (OWLDeclarationAxiomImpl.class, "entity", key);
	}


	/** {@inheritDoc} */
	@Override
	public boolean isDeclared (final OWLEntity owlEntity, final boolean includeImportsClosure)
	{
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
		{
			if (owlOntology.isDeclared (owlEntity))
				return true;
		}
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAsymmetricObjectPropertyAxiom> getAsymmetricObjectPropertyAxioms (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSet (OWLAsymmetricObjectPropertyAxiomImpl.class, OWLAsymmetricObjectPropertyAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAxiom> getAxioms ()
	{
		return this.db.retrieveSet (OWLAxiom.class, AXIOM_QUERY_STRING);
	}


	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends OWLAxiom> Set<T> getAxioms (final AxiomType<T> axiomType)
	{
		return AXIOM_TYPE_CLASSES.containsKey (axiomType) ? (Set<T>) this.db.retrieveSet (AXIOM_TYPE_CLASSES.get (axiomType)) : Collections.EMPTY_SET;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLClassAxiom> getAxioms (final OWLClass cls)
	{
		final Long key = this.getID (cls);
		if (key == null)
			return Collections.emptySet ();

		return this.db.execute (new HibernateWrapper<Set<OWLClassAxiom>> ()
		{
			@Override
			public Set<OWLClassAxiom> doInHibernate (final Session session)
			{
				final Set<OWLClassAxiom> result = new HashSet<OWLClassAxiom> ();
				result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLClassAxiom.class, EQUIVALENT_QUERY_STRING, key));
				result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLClassAxiom.class, DISJOINT_QUERY_STRING, key));
				result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLDisjointUnionAxiomImpl.class, "owlClass", key));
				result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLSubClassOfAxiomImpl.class, "subClass", key));
				return result;
			}
		});
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLObjectPropertyAxiom> getAxioms (final OWLObjectPropertyExpression prop)
	{
		final Long key = this.getID (prop);
		if (key == null)
			return Collections.emptySet ();

		final Set<OWLObjectPropertyAxiom> result = new HashSet<OWLObjectPropertyAxiom> ();

		// Any property characteristic axiom (i.e. Functional, Symmetric, Reflexive
		// etc.) whose subject is the specified property
		result.addAll (this.db.retrieveSet (OWLObjectPropertyCharacteristicAxiomImpl.class, "property", key));

		// Inefficient?
		final Set<OWLInverseObjectPropertiesAxiom> inverseProps = this.getInverseObjectPropertyAxioms (prop);

		// Sub-property axioms where the sub property is equal to the specified
		// property
		result.addAll (this.getObjectSubPropertyAxiomsForSubProperty (prop));
		// Equivalent property axioms where the axiom contains the specified
		// property
		result.addAll (this.getEquivalentObjectPropertiesAxioms (prop));
		// Equivalent property axioms that contain the inverse of the specified
		// property
		for (final OWLInverseObjectPropertiesAxiom inversePropAxiom: inverseProps)
			for (final OWLObjectPropertyExpression inverseProp: inversePropAxiom.getPropertiesMinus (prop))
				result.addAll (this.getEquivalentObjectPropertiesAxioms (inverseProp));

		// Disjoint property axioms that contain the specified property
		result.addAll (this.getDisjointObjectPropertiesAxioms (prop));
		// Domain axioms that specify a domain of the specified property
		result.addAll (this.getObjectPropertyDomainAxioms (prop));
		// Range axioms that specify a range of the specified property
		result.addAll (this.getObjectPropertyRangeAxioms (prop));
		// Inverse properties axioms that contain the specified property
		result.addAll (inverseProps);

		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDataPropertyAxiom> getAxioms (final OWLDataProperty prop)
	{
		if (prop == null)
			return Collections.emptySet ();

		final Set<OWLDataPropertyAxiom> result = new HashSet<OWLDataPropertyAxiom> ();
		// Sub-property axioms where the sub property is equal to the specified
		// property
		result.addAll (this.getDataSubPropertyAxiomsForSubProperty (prop));
		// Equivalent property axioms where the axiom contains the specified
		// property
		result.addAll (this.getEquivalentDataPropertiesAxioms (prop));
		// Disjoint property axioms that contain the specified property
		result.addAll (this.getDisjointDataPropertiesAxioms (prop));
		// Domain axioms that specify a domain of the specified property
		result.addAll (this.getDataPropertyDomainAxioms (prop));
		// Range axioms that specify a range of the specified property
		result.addAll (this.getDataPropertyRangeAxioms (prop));
		// Any property characteristic axiom (i.e. Functional, Symmetric, Reflexive
		// etc.) whose subject is the specified property
		result.addAll (this.getFunctionalDataPropertyAxioms (prop));

		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLIndividualAxiom> getAxioms (final OWLIndividual individual)
	{
		final Long key = this.getID (individual);
		if (key == null)
			return Collections.emptySet ();

		return this.db.execute (new HibernateWrapper<Set<OWLIndividualAxiom>> ()
		{
			@Override
			public Set<OWLIndividualAxiom> doInHibernate (final Session session)
			{
				final String naryIndiQueryString = individual instanceof OWLNamedIndividual ? NARY_NAMED_INDIVIDUAL_QUERY : NARY_ANONYMOUS_INDIVIDUAL_QUERY;
				final String queryIndiRelString = individual instanceof OWLNamedIndividual ? NARY_NAMED_INDIVIDUAL_REL_QUERY : NARY_ANONYMOUS_INDIVIDUAL_REL_QUERY;

				final Set<OWLIndividualAxiom> result = OWLDBOntology.this.db.retrieveSetById (session, OWLIndividualAxiom.class, naryIndiQueryString, key);

				// As HQL doesn't support unions we do a second Query for ClassAssertion
				// perhaps we could use some kind of view?
				result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLClassAssertionImpl.class, "individual", key));
				result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLClassAssertionImpl.class, queryIndiRelString, key));
				return result;
			}
		});
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAnnotationAxiom> getAxioms (final OWLAnnotationProperty property)
	{
		final Set<OWLAnnotationAxiom> result = new HashSet<OWLAnnotationAxiom> ();
		for (final OWLSubAnnotationPropertyOfAxiom ax: this.getAxioms (AxiomType.SUB_ANNOTATION_PROPERTY_OF))
		{
			if (ax.getSubProperty ().equals (property))
				result.add (ax);
		}
		for (final OWLAnnotationPropertyRangeAxiom ax: this.getAxioms (AxiomType.ANNOTATION_PROPERTY_RANGE))
		{
			if (ax.getProperty ().equals (property))
				result.add (ax);
		}
		for (final OWLAnnotationPropertyDomainAxiom ax: this.getAxioms (AxiomType.ANNOTATION_PROPERTY_DOMAIN))
		{
			if (ax.getProperty ().equals (property))
				result.add (ax);
		}
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDatatypeDefinitionAxiom> getAxioms (final OWLDatatype datatype)
	{
		return this.getDatatypeDefinitions (datatype);
	}


	/** {@inheritDoc} */
	@Override
	public <T extends OWLAxiom> Set<T> getAxioms (final AxiomType<T> axiomType, final boolean includeImportsClosure)
	{
		final Set<T> axioms = new HashSet<T> ();
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
			axioms.addAll (owlOntology.getAxioms (axiomType));
		return axioms;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAxiom> getAxiomsIgnoreAnnotations (final OWLAxiom axiom)
	{
		final Set<OWLAxiom> result = new HashSet<OWLAxiom> ();
		if (this.containsAxiom (axiom))
			result.add (axiom);

		// TODO How to implement this?
		/*
		 * final Set<OWLAxiom> annotated = logicalAxiom2AnnotatedAxiomMap.get
		 * (axiom.getAxiomWithoutAnnotations ()); if (annotated != null)
		 * result.addAll (annotated);
		 */
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAxiom> getAxiomsIgnoreAnnotations (final OWLAxiom axiom, final boolean includeImportsClosure)
	{
		final Set<OWLAxiom> result = new HashSet<OWLAxiom> ();
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
			result.addAll (owlOntology.getAxiomsIgnoreAnnotations (axiom));
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAxiom> getTBoxAxioms (final boolean includeImportsClosure)
	{
		final Set<OWLAxiom> result = new HashSet<OWLAxiom> ();
		for (final AxiomType<?> type: AxiomType.TBoxAxiomTypes)
			result.addAll (this.getAxioms (type, includeImportsClosure));
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAxiom> getABoxAxioms (final boolean includeImportsClosure)
	{
		final Set<OWLAxiom> result = new HashSet<OWLAxiom> ();
		for (final AxiomType<?> type: AxiomType.ABoxAxiomTypes)
			result.addAll (this.getAxioms (type, includeImportsClosure));
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAxiom> getRBoxAxioms (final boolean includeImportsClosure)
	{
		final Set<OWLAxiom> result = new HashSet<OWLAxiom> ();
		for (final AxiomType<?> type: AxiomType.RBoxAxiomTypes)
			result.addAll (this.getAxioms (type, includeImportsClosure));
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLClassAssertionAxiom> getClassAssertionAxioms (final OWLIndividual individual)
	{
		return this.retrieveSet (OWLClassAssertionImpl.class, OWLClassAssertionAxiom.class, "individual", individual);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLClassAssertionAxiom> getClassAssertionAxioms (final OWLClassExpression type)
	{
		return this.retrieveSet (OWLClassAssertionImpl.class, OWLClassAssertionAxiom.class, "classExpression", type);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionAxioms (final OWLIndividual individual)
	{
		return this.retrieveSet (OWLDataPropertyAssertionAxiomImpl.class, OWLDataPropertyAssertionAxiom.class, "subject", individual);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDataPropertyDomainAxiom> getDataPropertyDomainAxioms (final OWLDataProperty property)
	{
		return this.retrieveSet (OWLDataPropertyDomainAxiomImpl.class, OWLDataPropertyDomainAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDataPropertyRangeAxiom> getDataPropertyRangeAxioms (final OWLDataProperty property)
	{
		return this.retrieveSet (OWLDataPropertyRangeAxiomImpl.class, OWLDataPropertyRangeAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSubProperty (final OWLDataProperty lhsProperty)
	{
		return this.retrieveSet (OWLSubDataPropertyOfAxiomImpl.class, OWLSubDataPropertyOfAxiom.class, "subProperty", lhsProperty);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLSubDataPropertyOfAxiom> getDataSubPropertyAxiomsForSuperProperty (final OWLDataPropertyExpression property)
	{
		return this.retrieveSet (OWLSubDataPropertyOfAxiomImpl.class, OWLSubDataPropertyOfAxiom.class, "superProperty", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDeclarationAxiom> getDeclarationAxioms (final OWLEntity subject)
	{
		return this.retrieveSet (OWLDeclarationAxiomImpl.class, OWLDeclarationAxiom.class, "entity", subject);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDifferentIndividualsAxiom> getDifferentIndividualAxioms (final OWLIndividual individual)
	{
		final Long key = this.getID (individual);
		if (key == null)
			return Collections.emptySet ();

		return this.db.retrieveSetById (OWLDifferentIndividualsAxiom.class, new String []
		{
				DIFFERENT_NAMED_INDI_QUERYSTRING,
				DIFFERENT_ANONYMOUS_INDI_QUERYSTRING
		}, key);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDisjointClassesAxiom> getDisjointClassesAxioms (final OWLClass cls)
	{
		return this.retrieveSetById (OWLDisjointClassesAxiom.class, DISJOINT_CLASS_QUERY_STRING, cls);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDisjointDataPropertiesAxiom> getDisjointDataPropertiesAxioms (final OWLDataProperty property)
	{
		return this.retrieveSetById (OWLDisjointDataPropertiesAxiom.class, DISJOINT_DP_QUERY_STRING, property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDisjointObjectPropertiesAxiom> getDisjointObjectPropertiesAxioms (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSetById (OWLDisjointObjectPropertiesAxiom.class, DISJOINT_OP_QUERY_STRING, property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDisjointUnionAxiom> getDisjointUnionAxioms (final OWLClass cls)
	{
		return this.retrieveSet (OWLDisjointUnionAxiomImpl.class, OWLDisjointUnionAxiom.class, "owlClass", cls);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLEquivalentClassesAxiom> getEquivalentClassesAxioms (final OWLClass cls)
	{
		return this.retrieveSetById (OWLEquivalentClassesAxiom.class, EQUIVALENT_CLASS_QUERY_STRING, cls);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLEquivalentDataPropertiesAxiom> getEquivalentDataPropertiesAxioms (final OWLDataProperty property)
	{
		return this.retrieveSetById (OWLEquivalentDataPropertiesAxiom.class, EQUIVALENT_DP_QUERY_STRING, property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLEquivalentObjectPropertiesAxiom> getEquivalentObjectPropertiesAxioms (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSetById (OWLEquivalentObjectPropertiesAxiom.class, EQUIVALENT_OP_QUERY_STRING, property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLFunctionalDataPropertyAxiom> getFunctionalDataPropertyAxioms (final OWLDataPropertyExpression property)
	{
		return this.retrieveSet (OWLFunctionalDataPropertyAxiomImpl.class, OWLFunctionalDataPropertyAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLFunctionalObjectPropertyAxiom> getFunctionalObjectPropertyAxioms (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSet (OWLFunctionalObjectPropertyAxiomImpl.class, OWLFunctionalObjectPropertyAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLClassAxiom> getGeneralClassAxioms ()
	{
		// Subclass axioms that have a complex class as the subclass. Equivalent
		// class axioms that don't contain any named classes (OWLClasses). Disjoint
		// class axioms that don't contain any named classes (OWLClasses)
		return this.db.retrieveSet (OWLClassAxiom.class, new String []
		{
				QUERY_SUB_CLASS_STRING,
				QUERY_EQ_CLASS_STRING,
				QUERY_DIS_CLASS_STRING
		});
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLImportsDeclaration> getImportsDeclarations ()
	{
		final Set<OWLImportsDeclaration> result = new HashSet<OWLImportsDeclaration> ();
		for (final OWLImportsDeclaration ax: this.db.retrieveSet (OWLImportsDeclaration.class))
			result.add (OWLDBOntology.this.dataFactory.getOWLImportsDeclaration (ax.getIRI ()));
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLInverseFunctionalObjectPropertyAxiom> getInverseFunctionalObjectPropertyAxioms (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSet (OWLInverseFunctionalObjectPropertyAxiomImpl.class, OWLInverseFunctionalObjectPropertyAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLInverseObjectPropertiesAxiom> getInverseObjectPropertyAxioms (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSetById (OWLInverseObjectPropertiesAxiom.class, INVERSE_OP_QUERY_STRING, property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLIrreflexiveObjectPropertyAxiom> getIrreflexiveObjectPropertyAxioms (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSet (OWLIrreflexiveObjectPropertyAxiomImpl.class, OWLIrreflexiveObjectPropertyAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLLogicalAxiom> getLogicalAxioms ()
	{
		return this.db.retrieveSet (OWLLogicalAxiom.class);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLNegativeDataPropertyAssertionAxiom> getNegativeDataPropertyAssertionAxioms (final OWLIndividual individual)
	{
		return this.retrieveSet (OWLNegativeDataPropertyAssertionImplAxiom.class, OWLNegativeDataPropertyAssertionAxiom.class, "subject", individual);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLNegativeObjectPropertyAssertionAxiom> getNegativeObjectPropertyAssertionAxioms (final OWLIndividual individual)
	{
		return this.retrieveSet (OWLNegativeObjectPropertyAssertionAxiomImpl.class, OWLNegativeObjectPropertyAssertionAxiom.class, "subject", individual);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionAxioms (final OWLIndividual individual)
	{
		return this.retrieveSet (OWLObjectPropertyAssertionAxiomImpl.class, OWLObjectPropertyAssertionAxiom.class, "subject", individual);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLObjectPropertyDomainAxiom> getObjectPropertyDomainAxioms (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSet (OWLObjectPropertyDomainAxiomImpl.class, OWLObjectPropertyDomainAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLObjectPropertyRangeAxiom> getObjectPropertyRangeAxioms (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSet (OWLObjectPropertyRangeAxiomImpl.class, OWLObjectPropertyRangeAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSubProperty (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSet (OWLSubObjectPropertyOfAxiomImpl.class, OWLSubObjectPropertyOfAxiom.class, "subProperty", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLSubObjectPropertyOfAxiom> getObjectSubPropertyAxiomsForSuperProperty (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSet (OWLSubObjectPropertyOfAxiomImpl.class, OWLSubObjectPropertyOfAxiom.class, "superProperty", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLClass> getClassesInSignature ()
	{
		return new HashSet<OWLClass> (this.db.retrieveSet (OWLClassImpl.class));
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLClass> getClassesInSignature (final boolean includeImportsClosure)
	{
		final Set<OWLClass> clazzes = new HashSet<OWLClass> ();
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
			clazzes.addAll (owlOntology.getClassesInSignature ());
		return clazzes;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDataProperty> getDataPropertiesInSignature ()
	{
		return new HashSet<OWLDataProperty> (this.db.retrieveSet (OWLDataPropertyImpl.class));
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDataProperty> getDataPropertiesInSignature (final boolean includeImportsClosure)
	{
		final Set<OWLDataProperty> dps = new HashSet<OWLDataProperty> ();
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
			dps.addAll (owlOntology.getDataPropertiesInSignature ());
		return dps;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLNamedIndividual> getIndividualsInSignature ()
	{
		return new HashSet<OWLNamedIndividual> (this.db.retrieveSet (OWLNamedIndividualImpl.class));
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLNamedIndividual> getIndividualsInSignature (final boolean includeImportsClosure)
	{
		final Set<OWLNamedIndividual> individuals = new HashSet<OWLNamedIndividual> ();
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
			individuals.addAll (owlOntology.getIndividualsInSignature ());
		return individuals;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLObjectProperty> getObjectPropertiesInSignature ()
	{
		return new HashSet<OWLObjectProperty> (this.db.retrieveSet (OWLObjectPropertyImpl.class));
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLObjectProperty> getObjectPropertiesInSignature (final boolean includeImportsClosure)
	{
		final Set<OWLObjectProperty> ops = new HashSet<OWLObjectProperty> ();
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
			ops.addAll (owlOntology.getObjectPropertiesInSignature ());
		return ops;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAxiom> getReferencingAxioms (final OWLEntity owlEntity)
	{
		final Long key = this.getID (owlEntity);
		if (key == null)
			return Collections.emptySet ();

		return this.db.execute (new HibernateWrapper<Set<OWLAxiom>> ()
		{
			@Override
			public Set<OWLAxiom> doInHibernate (final Session session)
			{
				final Set<OWLAxiom> result = new HashSet<OWLAxiom> ();

				// Anotations & Declaration
				result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLAnnotationAssertionAxiomImpl.class, "subject", key));
				result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLDeclarationAxiomImpl.class, "entity", key));

				if (owlEntity instanceof OWLClass)
				{
					result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLAxiom.class, CLASS_AXIOM_QUERY, key));
					result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLDisjointUnionAxiomImpl.class, "owlClass", key));
					result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLAxiom.class, SUB_CLASS_AXIOM_QUERY, key));
					result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLClassAssertionImpl.class, "classExpression", key));
					result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLObjectPropertyRangeAxiomImpl.class, "range", key));
					result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLPropertyDomainAxiomImpl.class, "domain", key));
				}
				if (owlEntity instanceof OWLObjectProperty)
				{
					result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLIndividualRelationshipAxiomImpl.class, "property", key));
					result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLInverseObjectPropertiesAxiom.class, QUERY_NARY_OBJPROP_STRING, key));
					result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLInverseObjectPropertiesAxiom.class, QUERY_CHAIN_PROP_STRING, key));
					result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLObjectPropertyCharacteristicAxiomImpl.class, "property", key));
					result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLInverseObjectPropertiesAxiom.class, QUERY_OBJ_SUBPROP_STRING, key));
					result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLUnaryPropertyAxiomImpl.class, "property", key));
				}
				if (owlEntity instanceof OWLDataProperty)
				{
					result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLIndividualRelationshipAxiomImpl.class, "property", key));
					result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLDataPropertyCharacteristicAxiomImpl.class, "property", key));
					result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLInverseObjectPropertiesAxiom.class, QUERY_NARY_DATAPROP_STRING, key));
					result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLInverseObjectPropertiesAxiom.class, QUERY_DATA_SUBPROP_STRING, key));
					result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLUnaryPropertyAxiomImpl.class, "property", key));
				}
				if (owlEntity instanceof OWLNamedIndividual)
				{
					result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLClassAssertionImpl.class, "individual", key));
					result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLIndividualAxiom.class, QUERY_NARY_INDI_STRING, key));
					result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLIndividualAxiom.class, QUERY_INDI_REL_STRING, key));
				}
				if (owlEntity instanceof OWLDatatype)
				{
					result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLDataPropertyRangeAxiomImpl.class, "range", key));
				}

				return result;
			}
		});
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAxiom> getReferencingAxioms (final OWLEntity owlEntity, final boolean includeImportsClosure)
	{
		final Set<OWLAxiom> axioms = new HashSet<OWLAxiom> ();
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
			axioms.addAll (owlOntology.getReferencingAxioms (owlEntity));
		return axioms;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAxiom> getReferencingAxioms (final OWLAnonymousIndividual individual)
	{
		// TODO: Check if some axiom type is missing that can reference an anonymous
		// individual

		final Long key = this.getID (individual);
		if (key == null)
			return Collections.emptySet ();

		return this.db.execute (new HibernateWrapper<Set<OWLAxiom>> ()
		{
			@Override
			public Set<OWLAxiom> doInHibernate (final Session session)
			{
				final Set<OWLAxiom> result = new HashSet<OWLAxiom> ();
				result.addAll (OWLDBOntology.this.db.retrieveSet (session, OWLClassAssertionAxiom.class, "individual", key));
				result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLIndividualAxiom.class, QUERY_NARY_INDI_STRING, key));
				result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLIndividualAxiom.class, QUERY_INDI_REL_STRING, key));
				return result;
			}
		});
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLReflexiveObjectPropertyAxiom> getReflexiveObjectPropertyAxioms (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSet (OWLReflexiveObjectPropertyAxiomImpl.class, OWLReflexiveObjectPropertyAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLSameIndividualAxiom> getSameIndividualAxioms (final OWLIndividual individual)
	{
		final Long key = this.getID (individual);
		if (key == null)
			return Collections.emptySet ();

		return this.db.execute (new HibernateWrapper<Set<OWLSameIndividualAxiom>> ()
		{
			@Override
			public Set<OWLSameIndividualAxiom> doInHibernate (final Session session)
			{
				final Set<OWLSameIndividualAxiom> result = new HashSet<OWLSameIndividualAxiom> ();
				result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLSameIndividualAxiom.class, SAME_NAMED_INDI_QUERY_STRING, key));
				result.addAll (OWLDBOntology.this.db.retrieveSetById (session, OWLSameIndividualAxiom.class, SAME_ANONYMOUS_INDI_QUERY_STRING, key));
				return result;
			}
		});
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSubClass (final OWLClass cls)
	{
		return this.retrieveSet (OWLSubClassOfAxiomImpl.class, OWLSubClassOfAxiom.class, "subClass", cls);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLSubClassOfAxiom> getSubClassAxiomsForSuperClass (final OWLClass cls)
	{
		return this.retrieveSet (OWLSubClassOfAxiomImpl.class, OWLSubClassOfAxiom.class, "superClass", cls);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLSymmetricObjectPropertyAxiom> getSymmetricObjectPropertyAxioms (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSet (OWLSymmetricObjectPropertyAxiomImpl.class, OWLSymmetricObjectPropertyAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLTransitiveObjectPropertyAxiom> getTransitiveObjectPropertyAxioms (final OWLObjectPropertyExpression property)
	{
		return this.retrieveSet (OWLTransitiveObjectPropertyAxiomImpl.class, OWLTransitiveObjectPropertyAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public void accept (final OWLObjectVisitor visitor)
	{
		visitor.visit (this);
	}


	/** {@inheritDoc} */
	@Override
	public <O> O accept (final OWLObjectVisitorEx<O> visitor)
	{
		return visitor.visit (this);
	}


	/** {@inheritDoc} */
	@Override
	public int compareTo (final OWLObject object)
	{
		return object instanceof OWLDBOntology ? this.getOntologyID ().compareTo (((OWLDBOntology) object).getOntologyID ()) : 0;
	}


	/** {@inheritDoc} */
	@Override
	public boolean equals (final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null || this.getClass () != obj.getClass ())
			return false;
		return this.getOntologyID ().equals (((OWLDBOntology) obj).getOntologyID ());
	}


	/** {@inheritDoc} */
	@Override
	public int hashCode ()
	{
		final int prime = 31;
		final int result = 1;
		return prime * result + this.getOntologyID ().hashCode ();
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms (final OWLAnnotationSubject entity)
	{
		return this.retrieveSet (OWLAnnotationAssertionAxiomImpl.class, OWLAnnotationAssertionAxiom.class, "subject", entity);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAnnotationPropertyDomainAxiom> getAnnotationPropertyDomainAxioms (final OWLAnnotationProperty property)
	{
		return this.retrieveSet (OWLAnnotationPropertyDomainAxiomImpl.class, OWLAnnotationPropertyDomainAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAnnotationPropertyRangeAxiom> getAnnotationPropertyRangeAxioms (final OWLAnnotationProperty property)
	{
		return this.retrieveSet (OWLAnnotationPropertyRangeAxiomImpl.class, OWLAnnotationPropertyRangeAxiom.class, "property", property);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAnnotation> getAnnotations ()
	{
		final Set<OWLAnnotation> result = new HashSet<OWLAnnotation> ();
		final Set<OWLAnnotationAssertionAxiom> annotationAssertions = this.getAnnotationAssertionAxioms (this.getOntologyID ().getOntologyIRI ());
		for (final OWLAnnotationAssertionAxiom owlAnnotationAssertionAxiom: annotationAssertions)
			result.add (owlAnnotationAssertionAxiom.getAnnotation ());
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public int getAxiomCount ()
	{
		// It seems that Hibernate returns a list entry for each subclass
		// (assumption) which we add up, there is nowhere a documentation about this
		// behaviour
		int result = 0;
		for (final Number n: this.db.retrieveList (Number.class, AXIOM_COUNT_QUERY_STRING))
			result += n.intValue ();
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public <T extends OWLAxiom> int getAxiomCount (final AxiomType<T> axiomType)
	{
		String clazz = ((Class<? extends OWLAxiom>) AXIOM_TYPE_CLASSES.get (axiomType)).getName ();
		String countQuery = AXIOMTYPE_COUNT_QUERY_STRING_PREFIX + clazz + AXIOMTYPE_COUNT_QUERY_STRING_SUFFIX;
		return this.db.countQuery (countQuery).intValue ();
	}


	/** {@inheritDoc} */
	@Override
	public <T extends OWLAxiom> int getAxiomCount (final AxiomType<T> axiomType, final boolean includeImportsClosure)
	{
		int count = 0;
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
			count += owlOntology.getAxiomCount (axiomType);
		return count;
	}


	/** {@inheritDoc} */
	@Override
	public int getLogicalAxiomCount ()
	{
		String clazz = OWLLogicalAxiom.class.getName ();
		String countQuery = AXIOMTYPE_COUNT_QUERY_STRING_PREFIX + clazz + AXIOMTYPE_COUNT_QUERY_STRING_SUFFIX;
		return this.db.countQuery (countQuery).intValue ();
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDatatypeDefinitionAxiom> getDatatypeDefinitions (final OWLDatatype datatype)
	{
		return this.retrieveSet (OWLDatatypeDefinitionAxiomImpl.class, OWLDatatypeDefinitionAxiom.class, "datatype", datatype);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLOntology> getDirectImports ()
	{
		return this.ontologyManager.getDirectImports (this);
	}


	/** {@inheritDoc} */
	@Override
	public Set<IRI> getDirectImportsDocuments ()
	{
		final Set<IRI> result = new HashSet<IRI> ();
		final Set<OWLImportsDeclaration> imports = this.getImportsDeclarations ();
		for (final OWLImportsDeclaration importDeclaration: imports)
			result.add (importDeclaration.getIRI ());
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLEntity> getEntitiesInSignature (final IRI iri)
	{
		final Set<OWLEntity> result = new HashSet<OWLEntity> ();

		if (this.containsClassInSignature (iri))
			result.add (this.dataFactory.getOWLClass (iri));

		if (this.containsObjectPropertyInSignature (iri))
			result.add (this.dataFactory.getOWLObjectProperty (iri));

		if (this.containsDataPropertyInSignature (iri))
			result.add (this.dataFactory.getOWLDataProperty (iri));

		if (this.containsIndividualInSignature (iri))
			result.add (this.dataFactory.getOWLNamedIndividual (iri));

		if (this.containsDatatypeInSignature (iri))
			result.add (this.dataFactory.getOWLDatatype (iri));

		if (this.containsAnnotationPropertyInSignature (iri))
			result.add (this.dataFactory.getOWLAnnotationProperty (iri));

		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLEntity> getEntitiesInSignature (final IRI iri, final boolean includeImportsClosure)
	{
		final Set<OWLEntity> result = new HashSet<OWLEntity> ();
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
			result.addAll (owlOntology.getEntitiesInSignature (iri));
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLHasKeyAxiom> getHasKeyAxioms (final OWLClass cls)
	{
		return this.retrieveSet (OWLHasKeyAxiomImpl.class, OWLHasKeyAxiom.class, "expression", cls);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLOntology> getImports ()
	{
		return this.ontologyManager.getImports (this);
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLOntology> getImportsClosure ()
	{
		return this.ontologyManager.getImportsClosure (this);
	}


	/** {@inheritDoc} */
	@Override
	public OWLOntologyManager getOWLOntologyManager ()
	{
		return this.ontologyManager;
	}


	/** {@inheritDoc} */
	@Override
	public OWLOntologyID getOntologyID ()
	{
		return this.ontologyIRI == null ? null : this.ontologyIRI.getOntologyID ();
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAnnotationProperty> getAnnotationPropertiesInSignature ()
	{
		return new HashSet<OWLAnnotationProperty> (this.db.retrieveSet (OWLAnnotationPropertyImpl.class));
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLAnonymousIndividual> getReferencedAnonymousIndividuals ()
	{
		return new HashSet<OWLAnonymousIndividual> (this.db.retrieveSet (OWLAnonymousIndividualImpl.class));
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDatatype> getDatatypesInSignature ()
	{
		return new HashSet<OWLDatatype> (this.db.retrieveSet (OWLDatatypeImpl.class));
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLDatatype> getDatatypesInSignature (final boolean includeImportsClosure)
	{
		final Set<OWLDatatype> result = new HashSet<OWLDatatype> ();
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
			result.addAll (owlOntology.getDatatypesInSignature ());
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLSubAnnotationPropertyOfAxiom> getSubAnnotationPropertyOfAxioms (final OWLAnnotationProperty subProperty)
	{
		return this.retrieveSet (OWLSubAnnotationPropertyOfAxiomImpl.class, OWLSubAnnotationPropertyOfAxiom.class, "subProperty", subProperty);
	}


	/** {@inheritDoc} */
	@Override
	public boolean isAnonymous ()
	{
		return this.getOntologyID ().getOntologyIRI () == null;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isEmpty ()
	{
		final Integer countQuery = this.db.countQuery (AXIOM_COUNT_QUERY_STRING);
		return countQuery == null || countQuery.intValue () == 0;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLEntity> getSignature ()
	{
		final Set<OWLEntity> result = new HashSet<OWLEntity> ();
		result.addAll (this.getClassesInSignature ());
		result.addAll (this.getDataPropertiesInSignature ());
		result.addAll (this.getDatatypesInSignature ());
		result.addAll (this.getIndividualsInSignature ());
		result.addAll (this.getObjectPropertiesInSignature ());
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLEntity> getSignature (final boolean includeImportsClosure)
	{
		final Set<OWLEntity> result = new HashSet<OWLEntity> ();
		for (final OWLOntology owlOntology: this.getOntologies (includeImportsClosure))
			result.addAll (owlOntology.getSignature ());
		return result;
	}


	/** {@inheritDoc} */
	@Override
	public Set<OWLClassExpression> getNestedClassExpressions ()
	{
		final OWLClassExpressionCollector collector = new OWLClassExpressionCollector ();
		return this.accept (collector);
	}


	/** {@inheritDoc} */
	@Override
	public boolean isTopEntity ()
	{
		return false;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isBottomEntity ()
	{
		return false;
	}


	/**
	 * Returns true if this ontology contains the given OWL object.
	 * 
	 * @param owlObject The OWL object to check for
	 * @return True if this ontology contains the given OWL object.
	 */
	protected boolean containsOWLObject (final OWLObject owlObject)
	{
		return this.getID (owlObject) != null;
	}


	/**
	 * Gets the import closure if the parameter is true otherwise this ontology
	 * wrapped as a set.
	 * 
	 * @param includeImportsClosure Use import closure?
	 * @return The ontologies
	 */
	protected Set<OWLOntology> getOntologies (final boolean includeImportsClosure)
	{
		return includeImportsClosure ? this.getImportsClosure () : Collections.singleton ((OWLOntology) this);
	}


	/**
	 * Retrieve all instances of the given class as a set in a Hibernate
	 * transaction.
	 * 
	 * @param <T> The type of the class to query
	 * @param <U> The type of the interface to cast to
	 * @param clazz The class to query
	 * @param interf The interface to cast to
	 * @param associationPath A dot-seperated property path
	 * @param object The object from which to get the key
	 * @return The retrieved set
	 */
	protected <U, T extends U> Set<U> retrieveSet (final Class<T> clazz, final Class<U> interf, final String associationPath, final OWLObject object)
	{
		final Long key = this.getID (object);
		if (key == null)
			return Collections.emptySet ();
		return new HashSet<U> (this.db.retrieveSet (clazz, associationPath, key));
	}


	/**
	 * Retrieve all instances of the given class as a set in a Hibernate
	 * transaction.
	 * 
	 * @param <T> The type of the class to query
	 * @param clazz The class to query
	 * @param query The query to execute
	 * @param object The object from which to get the key
	 * @return The retrieved set
	 */
	protected <T> Set<T> retrieveSetById (final Class<T> clazz, final String query, final OWLObject object)
	{
		final Long key = this.getID (object);
		if (key == null)
			return Collections.emptySet ();
		return this.db.retrieveSetById (clazz, query, key);
	}


	/**
	 * Get the Id of the OWLObject in the database.
	 * 
	 * @param object The OWLObject
	 * @return The ID of the object in the database
	 */
	protected Long getID (final OWLObject object)
	{
		if (object == null)
			return null;

		return this.db.execute (new HibernateWrapper<Long> ()
		{
			@Override
			public Long doInHibernate (final Session session)
			{
				return AnnotatedOWLObject.getID (session, object);
			}
		});
	}


	/**
	 * Get the database util. Do only use for testing!
	 * 
	 * @return The db
	 */
	public HibernateUtil getDb ()
	{
		return this.db;
	}


	/**
	 * TODO: overrided manually and has to be implemented
	 */
	@Override
	public Set<OWLAnonymousIndividual> getAnonymousIndividuals() {
		// TODO Auto-generated method stub
		return null;
	}
}
