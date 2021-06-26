pragma solidity >=0.7.0 <0.9.0;

contract Storage {

    string initialOntology;
    string tBox;
    string aBox;
    string rBox;
    string sparqlUpdate;
    string[] public migrations;

    function setInitialOntology(string memory _initialOntology) public {
        initialOntology = _initialOntology;
    }

    function getInitialOntology() public view returns (string memory) {
        return initialOntology;
    }

    function setSparqlUpdate(string memory _sparqlUpdate) public {
        sparqlUpdate = _sparqlUpdate;
        migrations.push(_sparqlUpdate);
    }

    function getSparqlUpdate() public view returns (string memory) {
        return sparqlUpdate;
    }

    function getSparqlUpdateMigrations() public view returns (string[] memory) {
        // TODO: fix deserialization to support more than one migration
        return migrations;
    }
}
