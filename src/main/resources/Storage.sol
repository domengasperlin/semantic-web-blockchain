pragma solidity >=0.7.0 <0.9.0;

// SPDX-License-Identifier: UNLICENSED
contract Storage {
    string[] inputOntologies;
    string[] migrations;

    function addInputOntology(string memory _inputOntology) public {
        inputOntologies.push(_inputOntology);
    }

    function getInputOntologiesLength() public view returns (uint) {
        return inputOntologies.length;
    }

    function getInputOntology(uint index) public view returns (string memory) {
        if (index < inputOntologies.length){
            return inputOntologies[index];
        }
        return "";
    }

    function getSUPMigrationsLength() public view returns (uint) {
        return migrations.length;
    }

    function getSUPMigration(uint index) public view returns (string memory) {
        if (index < migrations.length){
            return migrations[index];
        }
        return "";
    }

    function addSUPMigration(string memory _sparqlUpdate) public {
        migrations.push(_sparqlUpdate);
    }
}
