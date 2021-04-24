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
The database must have a table (`storage.SQLTableName` in the properties file) with at least these 3 columns : 
* an INT id primary key (`storage.idColumnName`)
* a BLOB column for the 32 bytes index hashes (`storage.SQLVolumeIDColumnName`)
* a BLOB column for the document binaries (`storage.SQLDataColumnName`)

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

Configuration field name | Description | Notes
------------------------ | ----------- | -----
`crypto.hashAlgo` | Message digest algorithm to use in Merkle trees & index hashing | Must be compliant with the [Java](https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#messagedigest-algorithms) and [Javascript](https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/digest#supported_algorithms) implementations
`blockchain.privKey` | Private key of the ethereum account to sign transactions with | [Helpful link](https://vomtom.at/ethereum-private-and-public-keys/)
`blockchain.contractAddr` | The address of the DeSign smart contract to use | [Helpful link](https://ethereum.org/en/developers/docs/accounts/#types-of-account)
`blockchain.nodeURL` | URL of the node to send transactions to | [Helpful link](https://ethereum.org/en/developers/docs/nodes-and-clients/)
`blockchain.nodeURLForExternalChecks` | URL of the node that a [light web client](https://github.com/guillheu/DeSign#light-web-client) would use to connect to the blockchain | This can be useful to have be different than `blockchain.nodeURL`, for instance if the client is running it's own local node for which the link differs whether the call is made from the local network or from the internet. It's also possible that the client does not wish to have their local node used by external calls at all
`blockchain.gasPrice` | Gas price in Wei to use for transactions | Currently gas price is static. See [Future features](https://github.com/guillheu/DeSign#future-features)
`blockchain.gasLimit` | Maximum gas to use in a block | [Helpful link](https://ethereum.stackexchange.com/questions/50283/why-is-there-block-gas-limit#:~:text=gas%20limit%20of%20a%20block%20defines%20maximum%20gas,who%20could%20make%20an%20effective%20infinite%20transaction%20loop.)
`storage.SQLconnexionLink` | URL to connect to the SQL database | Username and password currently included in the link. See [Future features](https://github.com/guillheu/DeSign#future-features)
`storage.SQLDBName` | Name of the SQL database (scheme) to query | See [Setting up the SQL database](https://github.com/guillheu/DeSign#setting-up-the-sql-database)
`storage.SQLTableName` | Name of the table to query in the SQL database | See [Setting up the SQL database](https://github.com/guillheu/DeSign#setting-up-the-sql-database)
`storage.SQLVolumeIDColumnName` | Name of the document volume/index hash column in the given SQL table | See [Setting up the SQL database](https://github.com/guillheu/DeSign#setting-up-the-sql-database)
`storage.SQLDataColumnName` | Name of the data column in the given SQL table | See [Setting up the SQL database](https://github.com/guillheu/DeSign#setting-up-the-sql-database)
`storage.idColumnName` | Name of the unique document ID column in the given SQL table | See [Setting up the SQL database](https://github.com/guillheu/DeSign#setting-up-the-sql-database)
`storage.SQLDriver` | Name of the Java driver to use to interact with the SQL database | See [this list of drivers](https://www.roseindia.net/tutorial/java/jdbc/listofjdbcdriver.html). Note that not all drivers may be implemented.
`documents.defaultPath` | Default location to import files from and export proofs of signature to | Format may vary depending on operating system. be sure to include a final slash (`/`) or backslash (`\`)

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


* REST API
* ERC 725/735 for authentication, authorization and identidy management
* Third party signature request with timeout
* Multi-signature from several authorized parties
* Importing wallet file instead of requiring an ethereum private key in the config file
* Improved security for database login
* Variable gas price & gas limit
* More back-end storage support (local files, non-SQL databases...)
* Ability for the light web client to use a browser wallet (metamask) to connect to a network instead of the node URL provided in the proof of signature
* Auto-generating & modifying the config file from the client itself (initialization wizard)
* Decentralized proofs of signature (uploaded directly onto IPFS, requires either an IPFS node or the use of filecoins)

# FAQ

###**"What is a Merkle tree ? What is a Merkle path ?"**
[See this helpful link](https://medium.com/@jgm.orinoco/understanding-merkle-pollards-1547fc7efaa)


###**"Why sign Merkle tree roots instead of signing the documents directly ?"**
Signing a Merkle tree root is equivalent to signing all the documents that make up the Merkle tree, allowing us to reduce costs by sending a single transaction instead of potentially hundreds or thousands.


###**"Can I make a program that would verify the proof of signature for a given document ?"**
Yes, and we encourage you to ! The point of the proof of signature is to have the least equivocable way of identifying a signature. Our implementation (the [light web client](https://github.com/guillheu/DeSign#light-web-client)) was posted to IPFS precisely to reduce the likelyhood that someone somewhere may falsify the result being displayed on screen and make the proof more trustworthy and legally potent. Having third-party implementations of our proof of signature checker would further that aim.


###**"What node URL should I use ?"**
The node URL depends on the network you want to connect to. One way to get one is to use a service like [Infura](https://infura.io/), create a new project, select a network and copy the provided node URL.


###**"Why are there 2 node URLs in the config file ?"**
The `blockchain.nodeURL` is the URL of the node the client you're running will send transactions to.
The `blockchain.nodeURLForExternalChecks` is the URL that will be included in the proofs of signature


###**"Why use an external node URL instead of having the light web client use the local metamask installation ?"**
We want the signature checking process to be plug-and-play even for users that know nothing about the blockchain, thus it is necessary to specify in the signature proof which node URL is to be used to check a signature. That being said, we plan on implementing the ability for the light web client user [to choose between using the provided node URL, or using their browser wallet like metamask](https://github.com/guillheu/DeSign#future-features)


###**"I changed the configuration file, but my changes were not loaded into the app !"**
The executable jar heavy client will only load the config file on startup. The war file release however will load the config file each time a page is loaded.


###**"What's the difference between an index, a document volume and a document volume ID ?"**
A document volume is a set of document we wish to sign all at once (by signing the root of the Merkle tree built from them). The "index" is a unique identifier of a given document volume. It can be anything, a date, a name, or even song lyrics. A document volume ID is the hash of the index that will identify all the documents that belong to a document volume ; all the documents with the same volume ID (and therefor the same index) will be in the same document volume.