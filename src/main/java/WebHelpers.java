import contracts.Storage;
import org.apache.commons.io.FileUtils;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class WebHelpers {
    Web3j web3;
    Credentials credentials;
    public WebHelpers(Web3j web3, Credentials credentials) {
        this.web3 = web3;
        this.credentials = credentials;
    }

    public void deployStorageContract() {
        Storage helloWorld = null;
        try {
            helloWorld = Storage.deploy(web3, credentials, new DefaultGasProvider()).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File file = new File("rdf-sparql/output/tbox-axioms.nt");
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            TransactionReceipt storedTransactionReceipt = helloWorld.store(content).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadStorageContractAndCallRetrieveMethod(String contractAddress) {
        try {
            Storage storage = Storage.load(contractAddress, web3, credentials, new DefaultGasProvider());
            if (storage.isValid()) {
                String send = storage.retrieve().send();
                System.out.println(send);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
