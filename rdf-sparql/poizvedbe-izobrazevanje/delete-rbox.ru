prefix : <http://www.example.com/izobrazevanje#>
prefix owl: <http://www.w3.org/2002/07/owl#>
prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
prefix xml: <http://www.w3.org/XML/1998/namespace>
prefix xsd: <http://www.w3.org/2001/XMLSchema#>
prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
DELETE DATA {:jeBruc rdf:type owl:DatatypeProperty ;
                     rdfs:subPropertyOf owl:topDataProperty ;
                     rdfs:domain :Študent ;
                     rdfs:range xsd:boolean .}
