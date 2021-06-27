pragma solidity >=0.7.0 <0.9.0;

// SPDX-License-Identifier: UNLICENSED
contract Storage {

    string initialOntology;
    string sparqlUpdate;
    string[] migrations;

    function setInitialOntology(string memory _initialOntology) public {
        initialOntology = _initialOntology;
    }

    function getInitialOntology() public view returns (string memory) {
        return initialOntology;
    }

    function getMigrationsLength() public view returns (uint) {
        return migrations.length;
    }

    function getSparqlMigration(uint index) public view returns (string memory) {
        if (index < migrations.length){
            return migrations[index];
        }
        return "";
    }

    function setSparqlUpdate(string memory _sparqlUpdate) public {
        sparqlUpdate = _sparqlUpdate;
        migrations.push(_sparqlUpdate);
    }

    function getSparqlUpdate() public view returns (string memory) {
        return sparqlUpdate;
    }
}
