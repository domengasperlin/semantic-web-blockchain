prefix dbo: <http://dbpedia.org/ontology/>
prefix dbr: <http://dbpedia.org/resource/>
prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
prefix foaf: <http://xmlns.com/foaf/0.1/>

SELECT ?skladba ?domaca_stran ?trajanje WHERE {
    ?skladba rdf:type dbo:Song .
    ?skladba foaf:homepage ?domaca_stran .
    ?skladba <http://dbpedia.org/ontology/Work/runtime> ?trajanje .
} ORDER BY DESC(?trajanje) LIMIT 3