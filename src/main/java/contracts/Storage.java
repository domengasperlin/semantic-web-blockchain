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
    public static final String BINARY = "608060405234801561001057600080fd5b5061082c806100206000396000f3fe608060405234801561001057600080fd5b50600436106100ce5760003560e01c8063551ef8031161008c578063a0de815e11610066578063a0de815e1461016c578063b605294d1461017f578063f131c41714610192578063f9628550146101a557600080fd5b8063551ef8031461013e57806362c2b80b1461015157806372090cc91461016457600080fd5b80629f1940146100d3578063085f5cb6146100f15780631e5cb9be146100f957806324a220961461010c578063253a502d1461012157806354f194a414610136575b600080fd5b6100db6101ad565b6040516100e8919061078b565b60405180910390f35b6100db61023f565b6100db6101073660046106c7565b61024e565b61011f61011a366004610608565b6102fa565b005b610129610311565b6040516100e8919061072a565b6100db6103ea565b61011f61014c366004610608565b6103f9565b61011f61015f366004610608565b61040c565b6100db61041f565b61011f61017a366004610608565b61042e565b61011f61018d366004610643565b610441565b61011f6101a0366004610608565b610482565b6100db6104d9565b6060600080546101bc906107a5565b80601f01602080910402602001604051908101604052809291908181526020018280546101e8906107a5565b80156102355780601f1061020a57610100808354040283529160200191610235565b820191906000526020600020905b81548152906001019060200180831161021857829003601f168201915b5050505050905090565b6060600480546101bc906107a5565b6005818154811061025e57600080fd5b906000526020600020016000915090508054610279906107a5565b80601f01602080910402602001604051908101604052809291908181526020018280546102a5906107a5565b80156102f25780601f106102c7576101008083540402835291602001916102f2565b820191906000526020600020905b8154815290600101906020018083116102d557829003601f168201915b505050505081565b805161030d9060039060208401906104e8565b5050565b60606005805480602002602001604051908101604052809291908181526020016000905b828210156103e1578382906000526020600020018054610354906107a5565b80601f0160208091040260200160405190810160405280929190818152602001828054610380906107a5565b80156103cd5780601f106103a2576101008083540402835291602001916103cd565b820191906000526020600020905b8154815290600101906020018083116103b057829003601f168201915b505050505081526020019060010190610335565b50505050905090565b6060600280546101bc906107a5565b805161030d9060019060208401906104e8565b805161030d9060009060208401906104e8565b6060600380546101bc906107a5565b805161030d9060029060208401906104e8565b82516104549060019060208601906104e8565b5081516104689060029060208501906104e8565b50805161047c9060039060208401906104e8565b50505050565b80516104959060049060208401906104e8565b5060058054600181018255600091909152815161030d917f036b6384b5eca791c62761152d0c79bb0604c104a5fb6f4eb0703f3154bb3db0019060208401906104e8565b6060600180546101bc906107a5565b8280546104f4906107a5565b90600052602060002090601f016020900481019282610516576000855561055c565b82601f1061052f57805160ff191683800117855561055c565b8280016001018555821561055c579182015b8281111561055c578251825591602001919060010190610541565b5061056892915061056c565b5090565b5b80821115610568576000815560010161056d565b600082601f830112610591578081fd5b813567ffffffffffffffff808211156105ac576105ac6107e0565b604051601f8301601f19908116603f011681019082821181831017156105d4576105d46107e0565b816040528381528660208588010111156105ec578485fd5b8360208701602083013792830160200193909352509392505050565b600060208284031215610619578081fd5b813567ffffffffffffffff81111561062f578182fd5b61063b84828501610581565b949350505050565b600080600060608486031215610657578182fd5b833567ffffffffffffffff8082111561066e578384fd5b61067a87838801610581565b9450602086013591508082111561068f578384fd5b61069b87838801610581565b935060408601359150808211156106b0578283fd5b506106bd86828701610581565b9150509250925092565b6000602082840312156106d8578081fd5b5035919050565b60008151808452815b81811015610704576020818501810151868301820152016106e8565b818111156107155782602083870101525b50601f01601f19169290920160200192915050565b6000602080830181845280855180835260408601915060408160051b8701019250838701855b8281101561077e57603f1988860301845261076c8583516106df565b94509285019290850190600101610750565b5092979650505050505050565b60208152600061079e60208301846106df565b9392505050565b600181811c908216806107b957607f821691505b602082108114156107da57634e487b7160e01b600052602260045260246000fd5b50919050565b634e487b7160e01b600052604160045260246000fdfea264697066735822122092af72f1bab12bc755c8b8576cc8ca541069a832abb0d40fea48da6d62201b4e64736f6c63430008040033";

    public static final String FUNC_GETABOX = "getABox";

    public static final String FUNC_GETINITIALONTOLOGY = "getInitialOntology";

    public static final String FUNC_GETRBOX = "getRBox";

    public static final String FUNC_GETSPARQLUPDATE = "getSparqlUpdate";

    public static final String FUNC_GETSPARQLUPDATEMIGRATIONS = "getSparqlUpdateMigrations";

    public static final String FUNC_GETTBOX = "getTBox";

    public static final String FUNC_MIGRATIONS = "migrations";

    public static final String FUNC_SETABOX = "setABox";

    public static final String FUNC_SETINITIALONTOLOGY = "setInitialOntology";

    public static final String FUNC_SETRBOX = "setRBox";

    public static final String FUNC_SETSPARQLUPDATE = "setSparqlUpdate";

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

    public RemoteFunctionCall<String> getInitialOntology() {
        final Function function = new Function(FUNC_GETINITIALONTOLOGY, 
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

    public RemoteFunctionCall<String> getTBox() {
        final Function function = new Function(FUNC_GETTBOX, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> migrations(BigInteger param0) {
        final Function function = new Function(FUNC_MIGRATIONS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
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

    public RemoteFunctionCall<TransactionReceipt> setInitialOntology(String _initialOntology) {
        final Function function = new Function(
                FUNC_SETINITIALONTOLOGY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_initialOntology)), 
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

    public RemoteFunctionCall<TransactionReceipt> setSparqlUpdate(String _sparqlUpdate) {
        final Function function = new Function(
                FUNC_SETSPARQLUPDATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_sparqlUpdate)), 
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
