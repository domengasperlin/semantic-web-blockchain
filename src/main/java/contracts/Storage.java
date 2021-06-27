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
    public static final String BINARY = "608060405234801561001057600080fd5b506104f4806100206000396000f3fe608060405234801561001057600080fd5b50600436106100615760003560e01c80629f194014610066578063085f5cb614610084578063132497d91461008c57806362c2b80b1461009d578063967eaf41146100b2578063f131c417146100c5575b600080fd5b61006e6100d8565b60405161007b919061041a565b60405180910390f35b61006e61016a565b60025460405190815260200161007b565b6100b06100ab366004610358565b610179565b005b61006e6100c0366004610402565b610190565b6100b06100d3366004610358565b61026c565b6060600080546100e79061046d565b80601f01602080910402602001604051908101604052809291908181526020018280546101139061046d565b80156101605780601f1061013557610100808354040283529160200191610160565b820191906000526020600020905b81548152906001019060200180831161014357829003601f168201915b5050505050905090565b6060600180546100e79061046d565b805161018c9060009060208401906102bf565b5050565b60025460609082101561025857600282815481106101be57634e487b7160e01b600052603260045260246000fd5b9060005260206000200180546101d39061046d565b80601f01602080910402602001604051908101604052809291908181526020018280546101ff9061046d565b801561024c5780601f106102215761010080835404028352916020019161024c565b820191906000526020600020905b81548152906001019060200180831161022f57829003601f168201915b50505050509050919050565b505060408051602081019091526000815290565b805161027f9060019060208401906102bf565b5060028054600181018255600091909152815161018c917f405787fa12a823e0f2b7631cc41b3ba8828b3321ca811111fa75cd3aa3bb5ace019060208401905b8280546102cb9061046d565b90600052602060002090601f0160209004810192826102ed5760008555610333565b82601f1061030657805160ff1916838001178555610333565b82800160010185558215610333579182015b82811115610333578251825591602001919060010190610318565b5061033f929150610343565b5090565b5b8082111561033f5760008155600101610344565b600060208284031215610369578081fd5b813567ffffffffffffffff80821115610380578283fd5b818401915084601f830112610393578283fd5b8135818111156103a5576103a56104a8565b604051601f8201601f19908116603f011681019083821181831017156103cd576103cd6104a8565b816040528281528760208487010111156103e5578586fd5b826020860160208301379182016020019490945295945050505050565b600060208284031215610413578081fd5b5035919050565b6000602080835283518082850152825b818110156104465785810183015185820160400152820161042a565b818111156104575783604083870101525b50601f01601f1916929092016040019392505050565b600181811c9082168061048157607f821691505b602082108114156104a257634e487b7160e01b600052602260045260246000fd5b50919050565b634e487b7160e01b600052604160045260246000fdfea2646970667358221220b1cb5045a10e4d29cc01df097ce7b2af594d003f1d9bf25b38efa82424160c7964736f6c63430008040033";

    public static final String FUNC_GETINITIALONTOLOGY = "getInitialOntology";

    public static final String FUNC_GETMIGRATIONSLENGTH = "getMigrationsLength";

    public static final String FUNC_GETSPARQLMIGRATION = "getSparqlMigration";

    public static final String FUNC_GETSPARQLUPDATE = "getSparqlUpdate";

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

    public RemoteFunctionCall<BigInteger> getMigrationsLength() {
        final Function function = new Function(FUNC_GETMIGRATIONSLENGTH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> getSparqlMigration(BigInteger index) {
        final Function function = new Function(FUNC_GETSPARQLMIGRATION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getSparqlUpdate() {
        final Function function = new Function(FUNC_GETSPARQLUPDATE, 
                Arrays.<Type>asList(), 
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
