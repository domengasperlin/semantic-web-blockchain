contractLocation=../resources/Shramba.sol;
solc $contractLocation --bin --abi --optimize -o ../resources/ --overwrite
web3j generate solidity -b ../resources/Shramba.bin -a ../resources/Shramba.abi -o . -p contracts
