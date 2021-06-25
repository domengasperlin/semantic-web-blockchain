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
    public static final String BINARY = "608060405234801561001057600080fd5b506107ca806100206000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c8063551ef80311610071578063551ef8031461011157806372090cc914610124578063a0de815e1461012c578063b605294d1461013f578063f131c41714610152578063f96285501461016557600080fd5b8063085f5cb6146100ae5780631e5cb9be146100cc57806324a22096146100df578063253a502d146100f457806354f194a414610109575b600080fd5b6100b661016d565b6040516100c39190610729565b60405180910390f35b6100b66100da366004610665565b6101ff565b6100f26100ed3660046105a6565b6102ab565b005b6100fc6102c2565b6040516100c391906106c8565b6100b661039b565b6100f261011f3660046105a6565b6103aa565b6100b66103bd565b6100f261013a3660046105a6565b6103cc565b6100f261014d3660046105e1565b6103df565b6100f26101603660046105a6565b610420565b6100b6610477565b60606003805461017c90610743565b80601f01602080910402602001604051908101604052809291908181526020018280546101a890610743565b80156101f55780601f106101ca576101008083540402835291602001916101f5565b820191906000526020600020905b8154815290600101906020018083116101d857829003601f168201915b5050505050905090565b6004818154811061020f57600080fd5b90600052602060002001600091509050805461022a90610743565b80601f016020809104026020016040519081016040528092919081815260200182805461025690610743565b80156102a35780601f10610278576101008083540402835291602001916102a3565b820191906000526020600020905b81548152906001019060200180831161028657829003601f168201915b505050505081565b80516102be906002906020840190610486565b5050565b60606004805480602002602001604051908101604052809291908181526020016000905b8282101561039257838290600052602060002001805461030590610743565b80601f016020809104026020016040519081016040528092919081815260200182805461033190610743565b801561037e5780601f106103535761010080835404028352916020019161037e565b820191906000526020600020905b81548152906001019060200180831161036157829003601f168201915b5050505050815260200190600101906102e6565b50505050905090565b60606001805461017c90610743565b80516102be906000906020840190610486565b60606002805461017c90610743565b80516102be906001906020840190610486565b82516103f2906000906020860190610486565b508151610406906001906020850190610486565b50805161041a906002906020840190610486565b50505050565b8051610433906003906020840190610486565b506004805460018101825560009190915281516102be917f8a35acfbc15ff81a39ae7d344fd709f28e8600b4aa8c65c6b64bfe7fe36bd19b01906020840190610486565b60606000805461017c90610743565b82805461049290610743565b90600052602060002090601f0160209004810192826104b457600085556104fa565b82601f106104cd57805160ff19168380011785556104fa565b828001600101855582156104fa579182015b828111156104fa5782518255916020019190600101906104df565b5061050692915061050a565b5090565b5b80821115610506576000815560010161050b565b600082601f83011261052f578081fd5b813567ffffffffffffffff8082111561054a5761054a61077e565b604051601f8301601f19908116603f011681019082821181831017156105725761057261077e565b8160405283815286602085880101111561058a578485fd5b8360208701602083013792830160200193909352509392505050565b6000602082840312156105b7578081fd5b813567ffffffffffffffff8111156105cd578182fd5b6105d98482850161051f565b949350505050565b6000806000606084860312156105f5578182fd5b833567ffffffffffffffff8082111561060c578384fd5b6106188783880161051f565b9450602086013591508082111561062d578384fd5b6106398783880161051f565b9350604086013591508082111561064e578283fd5b5061065b8682870161051f565b9150509250925092565b600060208284031215610676578081fd5b5035919050565b60008151808452815b818110156106a257602081850181015186830182015201610686565b818111156106b35782602083870101525b50601f01601f19169290920160200192915050565b6000602080830181845280855180835260408601915060408160051b8701019250838701855b8281101561071c57603f1988860301845261070a85835161067d565b945092850192908501906001016106ee565b5092979650505050505050565b60208152600061073c602083018461067d565b9392505050565b600181811c9082168061075757607f821691505b6020821081141561077857634e487b7160e01b600052602260045260246000fd5b50919050565b634e487b7160e01b600052604160045260246000fdfea26469706673582212201adbb08e6bfb9d5af1bd31cdadc0ad82b9334942e5f6a19ff3986ad5694beab664736f6c63430008040033";

    public static final String FUNC_GETABOX = "getABox";

    public static final String FUNC_GETRBOX = "getRBox";

    public static final String FUNC_GETSPARQLUPDATE = "getSparqlUpdate";

    public static final String FUNC_GETSPARQLUPDATEMIGRATIONS = "getSparqlUpdateMigrations";

    public static final String FUNC_GETTBOX = "getTBox";

    public static final String FUNC_MIGRATIONS = "migrations";

    public static final String FUNC_SETABOX = "setABox";

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
