import contracts.Storage;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.math.BigInteger;

public class Web3Helpers {
    Web3j web3;
    Credentials credentials;
    private BigInteger gasLimit;
    private BigInteger gasPrice;
    public Web3Helpers(String ethereumNodeAddress, Boolean useGanacheSpecificGasPriceGasLimit) {
        this.web3 = Web3j.build(new HttpService(ethereumNodeAddress));
        if (useGanacheSpecificGasPriceGasLimit) {
            gasLimit = BigInteger.valueOf(6721975);
            gasPrice = Convert.toWei("20000000000", Convert.Unit.WEI).toBigInteger();
        } else {
            gasLimit = new DefaultGasProvider().getGasLimit();
            gasPrice = new DefaultGasProvider().getGasPrice();
        }
    }

    public void loadCredentials(ConfigLoader configLoader) {
        String ethereumWalletLocation = (String)configLoader.getEthereum().get("walletPath");
        String ethereumWalletPassword = (String)configLoader.getEthereum().get("walletPassword");
        String ethereumWalletPrivateKey = (String)configLoader.getEthereum().get("walletPrivateKey");

        if (ethereumWalletPrivateKey != null) {
            this.credentials = Credentials.create(ethereumWalletPrivateKey);
        }
        else if (ethereumWalletLocation != null) {
            try {
                this.credentials = WalletUtils.loadCredentials(ethereumWalletPassword, ethereumWalletLocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error fetching a wallet!");
        }
    }

    public String deployStorageContract() {
        Storage helloWorld = null;
        try {
            helloWorld = Storage.deploy(web3, credentials, gasPrice, gasLimit).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return helloWorld.getContractAddress();
    }

    public TransactionReceipt loadStorageContractAndCallStoreMethod(String contractAddress, String tBoxCID, String aBoxCID) {
        TransactionReceipt storedTransactionReceipt = null;
        try {
            Storage storage = Storage.load(contractAddress, web3, credentials, gasPrice, gasLimit);
            if (storage.isValid()) {
//                File file = new File(fileToBeStoredName);
//                String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                storedTransactionReceipt = storage.storeTBoxABox(tBoxCID, aBoxCID).send();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storedTransactionReceipt;
    }

    public String[] loadStorageContractAndCallRetrieveMethod(String contractAddress) {
        String tBoxCID = null;
        String aBoxCID = null;
        try {
            Storage storageContract = Storage.load(contractAddress, web3, credentials, gasPrice, gasLimit);
            if (storageContract.isValid()) {
                tBoxCID = storageContract.getTBox().send();
                aBoxCID = storageContract.getABox().send();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new String[]{tBoxCID, aBoxCID};
    }
}
