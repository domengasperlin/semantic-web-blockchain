import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multiaddr.MultiAddress;
import io.ipfs.multihash.Multihash;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IPFSHelpers {
    IPFS ipfs;

    public IPFSHelpers(ConfigLoader configLoader) {
        String IPFSNodeAddress = (String) configLoader.getIPFS().get("naslovVozlisca");
        MultiAddress multiAddress = new MultiAddress(IPFSNodeAddress);
        this.ipfs = new IPFS(multiAddress.getHost(), multiAddress.getTCPPort(), "/api/v0/", 600*1000, 600*1000, multiAddress.toString().contains("/https"));
        
    }

    public Multihash uploadLocalFileToIPFS(String fileName) {
        Multihash hash = null;
        try {
            NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File(fileName));
            MerkleNode addResult = ipfs.add(file).get(0);
            hash = addResult.hash;
            return hash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }


    public void retrieveFileAndSaveItToLocalSystem(String cid, String fileName) {
        Multihash filePointer = Multihash.fromBase58(cid);
        try {
            byte[] fileContents = ipfs.cat(filePointer);
            Files.write(Paths.get(fileName), fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
