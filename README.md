# DeSign
### A decentralized solution for digital signatures.

The intrinsic qualities (public, irreversible, irrepudiable) of any blockchain allow it to be a prime solution for storing digital signature.
This solution uses Ethereum smart contracts to run on any EVM blockchain and allow authorized addresses to sign document (hashes) onto the blockchain.
It also is meant to run directly onto a client's already existing infrastructure, and could theoretically handle millions or billions of document signatures each day (access to the database becomes the bottleneck, not the app itself).

The goal is to let an entity sign documents and generate proofs of their signature with legal footing.

## Features
* _GDPR compliant_ through the publication of hashes instead of raw documents
* Generating proofs of signature for individual documents without compromizing the confidentiality of the rest of the database
* Signing multiple documents at once through the use of a Merkle Tree
* Interfaces with any pre-existing SQL database
* Ability to specify a lifetime for signatures
* No reliance on centralized external agents ; this solution is meant to be deployed on premise
* Open-source and simple smart contract to facilitate interoperability with partners
* Java heavy client & ipfs web light client (proof of signature checking only)

# Installation
## Heavy client
### Setting up the SQL database
The releases only work with a pre-deployed SQL database.
The database must have a table (storage.SQLTableName in the properties file) with at least these 3 columns : 
* an INT id primary key (storage.idColumnName)
* a BLOB column for the 32 bytes index hashes (storage.SQLVolumeIDColumnName)
* a BLOB column for the document binaries (storage.SQLDataColumnName)

Here is an example of a working database :

![SQL database example](https://i.imgur.com/z3BD2Kq.png)

You must also specify the SQL driver name (storage.SQLDriver) in the properties file.
For example, it is `com.mysql.cj.jdbc.Driver` for a mysql database.

We heavily recommend that you use an external method to import documents into and manage your database. **The DeSign app is not an SQL database manager** ; in a production environment, it should only read from the database, not write to it.


You must have java available on your machine to run the runnable jar far release.
The war file release is to be deployed onto a TOMCAT server and offers a web GUI.

### The config.properties file
Here is a configuration file example :
```
crypto.hashAlgo = SHA-256
blockchain.privKey = [ETHEREUM PRIVATE KEY]
blockchain.contractAddr = 0xD8D74044703C2f98B38E048c639F2c32860cA278
blockchain.nodeURL = https://kovan.infura.io/v3/7cdcc900133c425fab136c45f004893b
blockchain.nodeURLForExternalChecks = https://kovan.infura.io/v3/7cdcc900133c425fab136c45f004893b
blockchain.gasPrice = 4100000000
blockchain.gasLimit = 12000000
storage.SQLconnexionLink = jdbc:mysql://127.0.0.1/?user=USER&password=PASSWORD
storage.SQLDBName = test
storage.SQLTableName = Documents
storage.SQLVolumeIDColumnName = indexHash
storage.SQLDataColumnName = data
storage.idColumnName = id
storage.SQLDriver = com.mysql.cj.jdbc.Driver
documents.defaultPath = /path/to/dir/
```

Configuration field name | Description | Example | Notes
------------------------ | ----------- | ------- | -----
`crypto.hashAlgo` | Message digest algorithm to use in Merkle trees & index hashing | `SHA-256` | Must be compliant with the [Java](https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#messagedigest-algorithms) and [Javascript](https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/digest#supported_algorithms) implementations
`blockchain.privKey` | Private key of the ethereum account to sign transactions with | 0x00112233445566778899AABBCCDDEEFF<br />00112233445566778899AABBCCDDEEFF (DO NOT USE THIS) | [Helpful link](https://vomtom.at/ethereum-private-and-public-keys/)
`blockchain.contractAddr` | The address of the DeSign smart contract to use | `0xD8D74044703C2f98B38E048c639F2c32860cA278` | [Helpful link](https://ethereum.org/en/developers/docs/accounts/#types-of-account)
`blockchain.nodeURL` | URL of the node to send transactions to | `https://kovan.infura.io/v3/7cdcc900133c425fab136c45f004893b` | [Helpful link](https://ethereum.org/en/developers/docs/nodes-and-clients/)
`blockchain.nodeURLForExternalChecks` |
`blockchain.gasPrice` |
`blockchain.gasLimit` |
`storage.SQLconnexionLink` |
`storage.SQLDBName` |
`storage.SQLTableName` |
`storage.SQLVolumeIDColumnName` |
`storage.SQLDataColumnName` |
`storage.idColumnName` |
`storage.SQLDriver` |
`documents.defaultPath` |

This file must be named `config.properties`.

To run the executable jar release, run this command :
`java -jar path/to/jar/DeSignApp.jar path/to/config/config.properties`
To run the TOMCAT war file, place the `config.properties` file in the  `[TOMCAT_FOLDER]/webapps/DeSignApp/WEB-INF/classes/` folder.


## Light web client
The light web client does not require any installation. Currently it only allows the user to check the validity of a signature from the original unaltered file and the proof of signature file.
Since this client only reads from the blockchain, it does not require metamask, and is perfectly transparent to the user.

[You can find the ipfs-hosted light web client here...](https://ipfs.io/ipfs/QmcLEpxfJ2BjQiDDDuAMMDU86JeUuqibMkv9gMJudTwPug?filename=userInterface.html)

[... Or check out it's source here](https://github.com/guillheu/DeSign/blob/main/userAPI/userInterface.html)


# Future features
* Importing wallet file instead of requiring an ethereum private key in the config file
* Improved security for database login
* ERC 725/735 for authentication, authorization and identidy management
* Third party signature request with timeout
* Multi-signature from several authorized parties
* Decentralized proofs of signature (uploaded directly onto IPFS, requires either an IPFS node or the use of filecoins)
* More back-end storage support (local files, non-SQL databases...)
* Auto-generating & modifying the config file from the client itself (initialization wizard)

# FAQ

"What node URL should I use ?"
The node URL depends on the network you want to connect to. One way to get one is to use a service like [Infura](https://infura.io/), create a new project, select a network and copy the provided node URL.

"Why are there 2 node URLs in the config file ?"
The `blockchain.nodeURL` is the URL of the node the client you're running will send transactions to.
The `blockchain.nodeURLForExternalChecks` is the URL that will be included in the proofs of signature