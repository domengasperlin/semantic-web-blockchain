import contracts.Storage;
import org.apache.commons.io.FileUtils;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class WebHelpers {
    Web3j web3;
    Credentials credentials;
    public WebHelpers(String ethereumNodeAddress, Credentials credentials) {
        this.web3 = Web3j.build(new HttpService(ethereumNodeAddress));
        this.credentials = credentials;
    }

    public String deployStorageContract() {
        Storage helloWorld = null;
        try {
            helloWorld = Storage.deploy(web3, credentials, new DefaultGasProvider()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return helloWorld.getContractAddress();
    }

    public TransactionReceipt loadStorageContractAndCallStoreMethod(String contractAddress, String tBoxCID, String aBoxCID) {
        TransactionReceipt storedTransactionReceipt = null;
        try {
            Storage storage = Storage.load(contractAddress, web3, credentials, new DefaultGasProvider());
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
            Storage storageContract = Storage.load(contractAddress, web3, credentials, new DefaultGasProvider());
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
