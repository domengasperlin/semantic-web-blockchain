pragma solidity >=0.7.0 <0.9.0;

// SPDX-License-Identifier: UNLICENSED
contract Shramba {
    string[] vhodneOntologije;
    string[] migracije;

    function dodajVhodnoOntologijo(string memory _vhodnaOntologija) public {
        vhodneOntologije.push(_vhodnaOntologija);
    }

    function pridobiDolzinoVhodnihOntologij() public view returns (uint) {
        return vhodneOntologije.length;
    }

    function pridobiVhodnoOntologijo(uint indeks) public view returns (string memory) {
        if (indeks < vhodneOntologije.length){
            return vhodneOntologije[indeks];
        }
        return "";
    }

    function pridobiDolzinoMigracij() public view returns (uint) {
        return migracije.length;
    }

    function pridobiMigracijo(uint indeks) public view returns (string memory) {
        if (indeks < migracije.length){
            return migracije[indeks];
        }
        return "";
    }

    function dodajMigracijo(string memory _migracija) public {
        migracije.push(_migracija);
    }
}
