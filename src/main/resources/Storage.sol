pragma solidity >=0.7.0 <0.9.0;

contract Storage {

    string tBox;
    string aBox;
    string rBox;
    string sparqlUpdate;
    string[] public migrations;

    function storeTBoxABoxRBox(string memory _tBox, string memory _aBox, string memory _rBox) public {
        tBox = _tBox;
        aBox = _aBox;
        rBox = _rBox;
    }

    function setTBox(string memory _tBox) public {
        tBox = _tBox;
    }

    function getTBox() public view returns (string memory) {
        return tBox;
    }

    function getABox() public view returns (string memory) {
        return aBox;
    }

    function setABox(string memory _aBox) public {
        aBox = _aBox;
    }

    function getRBox() public view returns (string memory) {
        return rBox;
    }

    function setRBox(string memory _rBox) public {
        rBox = _rBox;
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
