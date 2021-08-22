prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
SELECT ?something WHERE {
    <http://dbpedia.org/ontology/nominee> rdfs:isDefinedBy ?something
}