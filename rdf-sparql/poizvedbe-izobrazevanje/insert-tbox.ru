# MIGRACIJA
# DATUM: Thu Sep 23 17:34:49 CEST 2021
# SOL: s1ZngPgG3FdvxCqcNjZ4YKqZH9XX01
prefix : <http://www.semanticweb.org/domen/ontologies/2021/4/izobrazevanje#>
prefix owl: <http://www.w3.org/2002/07/owl#>
prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
prefix xml: <http://www.w3.org/XML/1998/namespace>
prefix xsd: <http://www.w3.org/2001/XMLSchema#>
prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
INSERT DATA
{
  :Magistrski rdf:type owl:Class ;
  rdfs:subClassOf :Študij .
  :Doktorski rdf:type owl:Class ;
     rdfs:subClassOf :Študij.
}