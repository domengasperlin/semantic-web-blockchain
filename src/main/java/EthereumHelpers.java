import contracts.Shramba;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.logging.Logger;

public class EthereumHelpers {
    Web3j web3;
    Credentials credentials;
    private BigInteger gasLimit;
    private BigInteger gasPrice;
    Shramba storageContract;
    private static final Logger log = Logger.getLogger(EthereumHelpers.class.getName());
    public EthereumHelpers(ConfigLoader configLoader) {
        String ethereumNodeAddress = (String) configLoader.getEthereum().get("naslovVozlisca");
        this.web3 = Web3j.build(new HttpService(ethereumNodeAddress));
        if (configLoader.isDevelopment()) {
            // Ganache specific
            gasLimit = BigInteger.valueOf(6721975);
            gasPrice = Convert.toWei("20000000000", Convert.Unit.WEI).toBigInteger();
        } else {
            gasLimit = new DefaultGasProvider().getGasLimit();
            gasPrice = new DefaultGasProvider().getGasPrice();
        }
        loadWalletCredentials(configLoader);
    }

    public void loadContractAtAddress(String contractAddress) {
        try {
            Shramba storageContr = Shramba.load(contractAddress, web3, credentials, gasPrice, gasLimit);
            if (storageContr.isValid()) {
                this.storageContract = storageContr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadWalletCredentials(ConfigLoader configLoader) {
        String ethereumWalletLocation = (String)configLoader.getEthereum().get("lokacijaDenarnice");
        String ethereumWalletPassword = (String)configLoader.getEthereum().get("gesloDenarnice");
        String ethereumWalletPrivateKey = (String)configLoader.getEthereum().get("zasebniKljucDenarnice");

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
            log.severe("Error fetching a wallet!");
        }
    }

    public String deployStorageContract() {
        Shramba helloWorld = null;
        try {
            helloWorld = Shramba.deploy(web3, credentials, gasPrice, gasLimit).send();
            storageContract = helloWorld;
        } catch (Exception e) {
            e.printStackTrace();
        }
        String contractAddress = helloWorld.getContractAddress();
        log.info("[ETH] contract address: " + contractAddress);
        loadContractAtAddress(contractAddress);
        return contractAddress;
    }

    public Shramba getContract() {
        return this.storageContract;
    }
}
