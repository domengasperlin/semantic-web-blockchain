pragma solidity >=0.7.0 <0.9.0;

contract Storage {

    string tBox;
    string aBox;

    function storeTBoxABox(string memory _tBox, string memory _aBox) public {
        tBox = _tBox;
        aBox = _aBox;
    }

    function getTBox() public view returns (string memory){
        return tBox;
    }

    function getABox() public view returns (string memory){
        return aBox;
    }
}