PREFIX dc: <http://purl.org/dc/elements/1.1/>
DELETE { <http://example/book> dc:creator "Creator". }
INSERT { <http://example/book> dc:creator "Inventor" . }
WHERE {  <http://example/book> dc:creator "Creator". }