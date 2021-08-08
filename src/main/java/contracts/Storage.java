package contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
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
    public static final String BINARY = "608060405234801561001057600080fd5b506104aa806100206000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c806331582e8f1461006757806369c6965b1461007c57806371309f60146100a5578063caa6aa01146100b8578063dd55787b146100cb578063fbd77691146100dd575b600080fd5b61007a61007536600461030e565b6100e5565b005b61008f61008a3660046103b8565b610129565b60405161009c91906103d0565b60405180910390f35b61007a6100b336600461030e565b610205565b61008f6100c63660046103b8565b610247565b6001545b60405190815260200161009c565b6000546100cf565b600080546001810182559080528151610125917f290decd9548b62a8d60345a988386fc84ba6bc95484008f6362f93160ef3e56301906020840190610275565b5050565b6000546060908210156101f1576000828154811061015757634e487b7160e01b600052603260045260246000fd5b90600052602060002001805461016c90610423565b80601f016020809104026020016040519081016040528092919081815260200182805461019890610423565b80156101e55780601f106101ba576101008083540402835291602001916101e5565b820191906000526020600020905b8154815290600101906020018083116101c857829003601f168201915b50505050509050919050565b505060408051602081019091526000815290565b6001805480820182556000919091528151610125917fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf601906020840190610275565b6001546060908210156101f1576001828154811061015757634e487b7160e01b600052603260045260246000fd5b82805461028190610423565b90600052602060002090601f0160209004810192826102a357600085556102e9565b82601f106102bc57805160ff19168380011785556102e9565b828001600101855582156102e9579182015b828111156102e95782518255916020019190600101906102ce565b506102f59291506102f9565b5090565b5b808211156102f557600081556001016102fa565b60006020828403121561031f578081fd5b813567ffffffffffffffff80821115610336578283fd5b818401915084601f830112610349578283fd5b81358181111561035b5761035b61045e565b604051601f8201601f19908116603f011681019083821181831017156103835761038361045e565b8160405282815287602084870101111561039b578586fd5b826020860160208301379182016020019490945295945050505050565b6000602082840312156103c9578081fd5b5035919050565b6000602080835283518082850152825b818110156103fc578581018301518582016040015282016103e0565b8181111561040d5783604083870101525b50601f01601f1916929092016040019392505050565b600181811c9082168061043757607f821691505b6020821081141561045857634e487b7160e01b600052602260045260246000fd5b50919050565b634e487b7160e01b600052604160045260246000fdfea26469706673582212209a4032565015d312895b4e1835efe13941d193adcdfa53fa9bcf25a8c2a7457964736f6c63430008040033";

    public static final String FUNC_ADDINPUTONTOLOGY = "addInputOntology";

    public static final String FUNC_ADDSUPMIGRATION = "addSUPMigration";

    public static final String FUNC_GETINPUTONTOLOGIESLENGTH = "getInputOntologiesLength";

    public static final String FUNC_GETINPUTONTOLOGY = "getInputOntology";

    public static final String FUNC_GETSUPMIGRATION = "getSUPMigration";

    public static final String FUNC_GETSUPMIGRATIONSLENGTH = "getSUPMigrationsLength";

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

    public RemoteFunctionCall<TransactionReceipt> addInputOntology(String _inputOntology) {
        final Function function = new Function(
                FUNC_ADDINPUTONTOLOGY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_inputOntology)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addSUPMigration(String _sparqlUpdate) {
        final Function function = new Function(
                FUNC_ADDSUPMIGRATION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_sparqlUpdate)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getInputOntologiesLength() {
        final Function function = new Function(FUNC_GETINPUTONTOLOGIESLENGTH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> getInputOntology(BigInteger index) {
        final Function function = new Function(FUNC_GETINPUTONTOLOGY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getSUPMigration(BigInteger index) {
        final Function function = new Function(FUNC_GETSUPMIGRATION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> getSUPMigrationsLength() {
        final Function function = new Function(FUNC_GETSUPMIGRATIONSLENGTH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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
