pragma solidity >=0.7.0 <0.9.0;

contract Storage {

    string tBox;
    string aBox;
    string rBox;

    function storeTBoxABox(string memory _tBox, string memory _aBox, string memory _rBox) public {
        tBox = _tBox;
        aBox = _aBox;
        rBox = _rBox;
    }

    function getTBox() public view returns (string memory) {
        return tBox;
    }

    function getABox() public view returns (string memory) {
        return aBox;
    }

    function getRBox() public view returns (string memory) {
        return rBox;
    }
}
