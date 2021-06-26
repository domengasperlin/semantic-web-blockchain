package contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
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
    public static final String BINARY = "608060405234801561001057600080fd5b50610614806100206000396000f3fe608060405234801561001057600080fd5b50600436106100615760003560e01c80629f194014610066578063085f5cb6146100845780631e5cb9be1461008c578063253a502d1461009f57806362c2b80b146100b4578063f131c417146100c9575b600080fd5b61006e6100dc565b60405161007b9190610573565b60405180910390f35b61006e61016e565b61006e61009a3660046104af565b61017d565b6100a7610229565b60405161007b9190610512565b6100c76100c2366004610405565b610302565b005b6100c76100d7366004610405565b610319565b6060600080546100eb9061058d565b80601f01602080910402602001604051908101604052809291908181526020018280546101179061058d565b80156101645780601f1061013957610100808354040283529160200191610164565b820191906000526020600020905b81548152906001019060200180831161014757829003601f168201915b5050505050905090565b6060600480546100eb9061058d565b6005818154811061018d57600080fd5b9060005260206000200160009150905080546101a89061058d565b80601f01602080910402602001604051908101604052809291908181526020018280546101d49061058d565b80156102215780601f106101f657610100808354040283529160200191610221565b820191906000526020600020905b81548152906001019060200180831161020457829003601f168201915b505050505081565b60606005805480602002602001604051908101604052809291908181526020016000905b828210156102f957838290600052602060002001805461026c9061058d565b80601f01602080910402602001604051908101604052809291908181526020018280546102989061058d565b80156102e55780601f106102ba576101008083540402835291602001916102e5565b820191906000526020600020905b8154815290600101906020018083116102c857829003601f168201915b50505050508152602001906001019061024d565b50505050905090565b805161031590600090602084019061036c565b5050565b805161032c90600490602084019061036c565b50600580546001810182556000919091528151610315917f036b6384b5eca791c62761152d0c79bb0604c104a5fb6f4eb0703f3154bb3db0019060208401905b8280546103789061058d565b90600052602060002090601f01602090048101928261039a57600085556103e0565b82601f106103b357805160ff19168380011785556103e0565b828001600101855582156103e0579182015b828111156103e05782518255916020019190600101906103c5565b506103ec9291506103f0565b5090565b5b808211156103ec57600081556001016103f1565b600060208284031215610416578081fd5b813567ffffffffffffffff8082111561042d578283fd5b818401915084601f830112610440578283fd5b813581811115610452576104526105c8565b604051601f8201601f19908116603f0116810190838211818310171561047a5761047a6105c8565b81604052828152876020848701011115610492578586fd5b826020860160208301379182016020019490945295945050505050565b6000602082840312156104c0578081fd5b5035919050565b60008151808452815b818110156104ec576020818501810151868301820152016104d0565b818111156104fd5782602083870101525b50601f01601f19169290920160200192915050565b6000602080830181845280855180835260408601915060408160051b8701019250838701855b8281101561056657603f198886030184526105548583516104c7565b94509285019290850190600101610538565b5092979650505050505050565b60208152600061058660208301846104c7565b9392505050565b600181811c908216806105a157607f821691505b602082108114156105c257634e487b7160e01b600052602260045260246000fd5b50919050565b634e487b7160e01b600052604160045260246000fdfea264697066735822122067deb3080812436e58d3c98bf2bb0b184819b4a5e450a6ca5feaccc6dd99b27d64736f6c63430008040033";

    public static final String FUNC_GETINITIALONTOLOGY = "getInitialOntology";

    public static final String FUNC_GETSPARQLUPDATE = "getSparqlUpdate";

    public static final String FUNC_GETSPARQLUPDATEMIGRATIONS = "getSparqlUpdateMigrations";

    public static final String FUNC_MIGRATIONS = "migrations";

    public static final String FUNC_SETINITIALONTOLOGY = "setInitialOntology";

    public static final String FUNC_SETSPARQLUPDATE = "setSparqlUpdate";

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

    public RemoteFunctionCall<String> getInitialOntology() {
        final Function function = new Function(FUNC_GETINITIALONTOLOGY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getSparqlUpdate() {
        final Function function = new Function(FUNC_GETSPARQLUPDATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<List> getSparqlUpdateMigrations() {
        final Function function = new Function(FUNC_GETSPARQLUPDATEMIGRATIONS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<String> migrations(BigInteger param0) {
        final Function function = new Function(FUNC_MIGRATIONS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setInitialOntology(String _initialOntology) {
        final Function function = new Function(
                FUNC_SETINITIALONTOLOGY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_initialOntology)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setSparqlUpdate(String _sparqlUpdate) {
        final Function function = new Function(
                FUNC_SETSPARQLUPDATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_sparqlUpdate)), 
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
