package contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.6.4.
 */
@SuppressWarnings("rawtypes")
public class Storage extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b506102e4806100206000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c8063131a06801461003b5780632e64cec1146100e3575b600080fd5b6100e16004803603602081101561005157600080fd5b81019060208101813564010000000081111561006c57600080fd5b82018360208201111561007e57600080fd5b803590602001918460018302840111640100000000831117156100a057600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610160945050505050565b005b6100eb610177565b6040805160208082528351818301528351919283929083019185019080838360005b8381101561012557818101518382015260200161010d565b50505050905090810190601f1680156101525780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b805161017390600090602084019061020d565b5050565b60008054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156102035780601f106101d857610100808354040283529160200191610203565b820191906000526020600020905b8154815290600101906020018083116101e657829003601f168201915b5050505050905090565b828054600181600116156101000203166002900490600052602060002090601f0160209004810192826102435760008555610289565b82601f1061025c57805160ff1916838001178555610289565b82800160010185558215610289579182015b8281111561028957825182559160200191906001019061026e565b50610295929150610299565b5090565b5b80821115610295576000815560010161029a56fea26469706673582212201b03ec55f336eaf4668d45600ff940a18985769ac7a8e1a3bfe3962a074fb8fe64736f6c63430007050033";

    public static final String FUNC_RETRIEVE = "retrieve";

    public static final String FUNC_STORE = "store";

    @Deprecated
    protected Storage(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Storage(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Storage(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Storage(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> retrieve() {
        final Function function = new Function(FUNC_RETRIEVE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> store(String schema) {
        final Function function = new Function(
                FUNC_STORE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(schema)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static Storage load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Storage(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Storage load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Storage(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Storage load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Storage(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Storage load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Storage(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Storage> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Storage.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Storage> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Storage.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Storage> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Storage.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Storage> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Storage.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
