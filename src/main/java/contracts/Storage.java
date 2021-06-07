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
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class Storage extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b506104e5806100206000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c806372090cc91161005b57806372090cc9146100c8578063a0de815e146100d0578063b605294d146100e3578063f9628550146100f657600080fd5b806324a220961461008257806354f194a414610097578063551ef803146100b5575b600080fd5b61009561009036600461034c565b6100fe565b005b61009f610115565b6040516100ac919061040b565b60405180910390f35b6100956100c336600461034c565b6101a7565b61009f6101ba565b6100956100de36600461034c565b6101c9565b6100956100f1366004610387565b6101dc565b61009f61021d565b805161011190600290602084019061022c565b5050565b6060600180546101249061045e565b80601f01602080910402602001604051908101604052809291908181526020018280546101509061045e565b801561019d5780601f106101725761010080835404028352916020019161019d565b820191906000526020600020905b81548152906001019060200180831161018057829003601f168201915b5050505050905090565b805161011190600090602084019061022c565b6060600280546101249061045e565b805161011190600190602084019061022c565b82516101ef90600090602086019061022c565b50815161020390600190602085019061022c565b50805161021790600290602084019061022c565b50505050565b6060600080546101249061045e565b8280546102389061045e565b90600052602060002090601f01602090048101928261025a57600085556102a0565b82601f1061027357805160ff19168380011785556102a0565b828001600101855582156102a0579182015b828111156102a0578251825591602001919060010190610285565b506102ac9291506102b0565b5090565b5b808211156102ac57600081556001016102b1565b600082601f8301126102d5578081fd5b813567ffffffffffffffff808211156102f0576102f0610499565b604051601f8301601f19908116603f0116810190828211818310171561031857610318610499565b81604052838152866020858801011115610330578485fd5b8360208701602083013792830160200193909352509392505050565b60006020828403121561035d578081fd5b813567ffffffffffffffff811115610373578182fd5b61037f848285016102c5565b949350505050565b60008060006060848603121561039b578182fd5b833567ffffffffffffffff808211156103b2578384fd5b6103be878388016102c5565b945060208601359150808211156103d3578384fd5b6103df878388016102c5565b935060408601359150808211156103f4578283fd5b50610401868287016102c5565b9150509250925092565b6000602080835283518082850152825b818110156104375785810183015185820160400152820161041b565b818111156104485783604083870101525b50601f01601f1916929092016040019392505050565b600181811c9082168061047257607f821691505b6020821081141561049357634e487b7160e01b600052602260045260246000fd5b50919050565b634e487b7160e01b600052604160045260246000fdfea2646970667358221220bb92ef4994c540de544f180a6cc9650e0d251e9225614fcbccb2dd380c9c87c964736f6c63430008040033";

    public static final String FUNC_GETABOX = "getABox";

    public static final String FUNC_GETRBOX = "getRBox";

    public static final String FUNC_GETTBOX = "getTBox";

    public static final String FUNC_SETABOX = "setABox";

    public static final String FUNC_SETRBOX = "setRBox";

    public static final String FUNC_SETTBOX = "setTBox";

    public static final String FUNC_STORETBOXABOXRBOX = "storeTBoxABoxRBox";

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

    public RemoteFunctionCall<String> getABox() {
        final Function function = new Function(FUNC_GETABOX, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getRBox() {
        final Function function = new Function(FUNC_GETRBOX, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getTBox() {
        final Function function = new Function(FUNC_GETTBOX, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setABox(String _aBox) {
        final Function function = new Function(
                FUNC_SETABOX, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_aBox)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setRBox(String _rBox) {
        final Function function = new Function(
                FUNC_SETRBOX, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_rBox)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setTBox(String _tBox) {
        final Function function = new Function(
                FUNC_SETTBOX, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_tBox)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> storeTBoxABoxRBox(String _tBox, String _aBox, String _rBox) {
        final Function function = new Function(
                FUNC_STORETBOXABOXRBOX, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_tBox), 
                new org.web3j.abi.datatypes.Utf8String(_aBox), 
                new org.web3j.abi.datatypes.Utf8String(_rBox)), 
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
