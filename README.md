# Master's thesis 
Title: Integracija verige blokov in tehnologij semantičnega spleta

Title (English): Integration of blockchain and semantic web technologies

# Get up and running

### 
#### Install [solidity compiler](https://docs.soliditylang.org/en/v0.8.0/installing-solidity.html)
#### Install [web3j](http://docs.web3j.io/latest/quickstart/)
#### Generate smart contract wrappers
```bash
cd src/main/java;
./generateWrappers.sh
```

### Run Ethereum node with Ganache (local node)
- cli
```bash
npm install -g ganache-cli
ganache-cli -h 0.0.0.0 -d -m "example onion where village dignity affair lady inject spray car bomb two"
# set ethereumNodeAddress to "http://localhost:8545"
```
- gui
[Ganache](https://www.trufflesuite.com/ganache)

### Run Ethereum node on Infura (hosted node)
In order to connect to other Ethereum node change ethereum/nodeAddress in config.yaml

### Run IPFS node
[IPFS](https://ipfs.io/#install)

### Generating LUBM dataset
```bash
java -cp classes/ edu.lehigh.swat.bench.uba.Generator -univ 1 -seed 0 -onto http://swat.cse.lehigh.edu/onto/univ-bench.owl
```

### Loading rdf data into database quickly
[tdbloader](https://jena.apache.org/documentation/tdb/commands.html#installation)
Load toy ontology 
```bash
tdb2.tdbloader --loc ./target/dataset ./rdf-sparql/ontologija-izobrazevanje/izobrazevanje.ttl
```
or subset of DBpedia
```bash
tdb2.tdbloader --loc ./target/dataset ./rdf-sparql/ontologija-dbpedia/ABox_DBpedia_instance-types_lang=en_specific.ttl.gz 
tdb2.tdbloader --loc ./target/dataset ./rdf-sparql/ontologija-dbpedia/TBox_DBpedia_ontology_type=parsed.xml
```