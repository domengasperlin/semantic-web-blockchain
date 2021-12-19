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
public class Shramba extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b506104a6806100206000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80631cfacd98146100675780632864adb114610090578063353acc5e146100a3578063354a5e03146100b8578063631be332146100ca5780638d889159146100dd575b600080fd5b61007a6100753660046103b4565b6100e5565b60405161008791906103cc565b60405180910390f35b61007a61009e3660046103b4565b6101c1565b6100b66100b136600461030a565b6101ef565b005b6000545b604051908152602001610087565b6100b66100d836600461030a565b610233565b6001546100bc565b6000546060908210156101ad576000828154811061011357634e487b7160e01b600052603260045260246000fd5b9060005260206000200180546101289061041f565b80601f01602080910402602001604051908101604052809291908181526020018280546101549061041f565b80156101a15780601f10610176576101008083540402835291602001916101a1565b820191906000526020600020905b81548152906001019060200180831161018457829003601f168201915b50505050509050919050565b505060408051602081019091526000815290565b6001546060908210156101ad576001828154811061011357634e487b7160e01b600052603260045260246000fd5b60008054600181018255908052815161022f917f290decd9548b62a8d60345a988386fc84ba6bc95484008f6362f93160ef3e56301906020840190610271565b5050565b600180548082018255600091909152815161022f917fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf6019060208401905b82805461027d9061041f565b90600052602060002090601f01602090048101928261029f57600085556102e5565b82601f106102b857805160ff19168380011785556102e5565b828001600101855582156102e5579182015b828111156102e55782518255916020019190600101906102ca565b506102f19291506102f5565b5090565b5b808211156102f157600081556001016102f6565b60006020828403121561031b578081fd5b813567ffffffffffffffff80821115610332578283fd5b818401915084601f830112610345578283fd5b8135818111156103575761035761045a565b604051601f8201601f19908116603f0116810190838211818310171561037f5761037f61045a565b81604052828152876020848701011115610397578586fd5b826020860160208301379182016020019490945295945050505050565b6000602082840312156103c5578081fd5b5035919050565b6000602080835283518082850152825b818110156103f8578581018301518582016040015282016103dc565b818111156104095783604083870101525b50601f01601f1916929092016040019392505050565b600181811c9082168061043357607f821691505b6020821081141561045457634e487b7160e01b600052602260045260246000fd5b50919050565b634e487b7160e01b600052604160045260246000fdfea2646970667358221220cf267611043d5f54abf13763d505b14f2b34e936454cbe18d32d93c364ddcea664736f6c63430008040033";

    public static final String FUNC_DODAJMIGRACIJO = "dodajMigracijo";

    public static final String FUNC_DODAJVHODNOONTOLOGIJO = "dodajVhodnoOntologijo";

    public static final String FUNC_PRIDOBIDOLZINOMIGRACIJ = "pridobiDolzinoMigracij";

    public static final String FUNC_PRIDOBIDOLZINOVHODNIHONTOLOGIJ = "pridobiDolzinoVhodnihOntologij";

    public static final String FUNC_PRIDOBIMIGRACIJO = "pridobiMigracijo";

    public static final String FUNC_PRIDOBIVHODNOONTOLOGIJO = "pridobiVhodnoOntologijo";

    @Deprecated
    protected Shramba(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Shramba(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Shramba(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Shramba(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> dodajMigracijo(String _migracija) {
        final Function function = new Function(
                FUNC_DODAJMIGRACIJO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_migracija)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> dodajVhodnoOntologijo(String _vhodnaOntologija) {
        final Function function = new Function(
                FUNC_DODAJVHODNOONTOLOGIJO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_vhodnaOntologija)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> pridobiDolzinoMigracij() {
        final Function function = new Function(FUNC_PRIDOBIDOLZINOMIGRACIJ, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> pridobiDolzinoVhodnihOntologij() {
        final Function function = new Function(FUNC_PRIDOBIDOLZINOVHODNIHONTOLOGIJ, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> pridobiMigracijo(BigInteger indeks) {
        final Function function = new Function(FUNC_PRIDOBIMIGRACIJO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(indeks)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> pridobiVhodnoOntologijo(BigInteger indeks) {
        final Function function = new Function(FUNC_PRIDOBIVHODNOONTOLOGIJO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(indeks)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    @Deprecated
    public static Shramba load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Shramba(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Shramba load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Shramba(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Shramba load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Shramba(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Shramba load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Shramba(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Shramba> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Shramba.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Shramba> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Shramba.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Shramba> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Shramba.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Shramba> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Shramba.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
