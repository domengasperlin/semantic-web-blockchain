solc ../resources/Storage.sol --bin --abi --optimize -o ../resources/ --overwrite
web3j generate solidity -b ../resources/Storage.bin -a ../resources/Storage.abi -o . -p contracts
