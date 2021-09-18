import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
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
        this.ipfs = new IPFS(IPFSNodeAddress);
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


    public void uploadByteFile(String fileName, String content) {
        try {
            NamedStreamable.ByteArrayWrapper file = new NamedStreamable.ByteArrayWrapper(fileName, content.getBytes());
            MerkleNode addResult = ipfs.add(file).get(0);
            System.out.println(addResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String retrieveFileContents(String cid) {
        Multihash filePointer = Multihash.fromBase58(cid);
        try {
            byte[] fileContents = ipfs.cat(filePointer);
            String fileContentsToString = new String(fileContents, StandardCharsets.UTF_8);
            return fileContentsToString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void retrieveFileAndSaveItToLocalSystem(String cid, String fileName) {
        Multihash filePointer = Multihash.fromBase58(cid);
        try {
            byte[] fileContents = ipfs.cat(filePointer);
            Files.write( Paths.get(fileName), fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
