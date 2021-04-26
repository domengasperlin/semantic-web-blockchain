pragma solidity >=0.7.0 <0.9.0;

contract Storage {

    string tBoxSchema;

    function store(string memory schema) public {
        tBoxSchema = schema;
    }

    function retrieve() public view returns (string memory){
        return tBoxSchema;
    }
}