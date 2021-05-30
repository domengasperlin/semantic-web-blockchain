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


### Loading rdf data into database quickly
[tdbloader2](https://jena.apache.org/documentation/tdb/commands.html#installation)
```bash
./tdbloader2 --loc ./semantic-web-blockchain/target/dataset ./semantic-web-blockchain/rdf-sparql/input/ABox_DBpedia_instance-types_lang=en_specific.ttl.gz
```