# DeSign
decentralized signature.

signature proof checker on IPFS : https://ipfs.io/ipfs/QmcLEpxfJ2BjQiDDDuAMMDU86JeUuqibMkv9gMJudTwPug?filename=userInterface.html


#Installation
The releases only work with a pre-deployed SQL database.
The database must have a table (storage.SQLTableName in the properties file) with at least these 3 columns : 
* an INT id primary key (storage.idColumnName)
* a BLOB column for the 32 bytes index hashes (storage.SQLVolumeIDColumnName)
* a BLOB column for the document binaries (storage.SQLDataColumnName)

Here is an example of a working database :

![SQL database example](https://i.imgur.com/z3BD2Kq.png)

You must also specify the SQL driver name (storage.SQLDriver) in the properties file.
for example, it is "com.mysql.cj.jdbc.Driver" for a mysql database.

We heavily recommend that you use an external method to import documents into and manage your database. **The DeSign app is not an SQL database manager** ; in a production environment, it should only read from the database, not write to it.


You must have java available on your machine to run the runnable jar far release.
The war file release is to be deployed onto a TOMCAT server and offers a web GUI.

#The config.properties file
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

this file must be named `config.properties` and placed in the same folder as the jar executable, or in your `[TOMCAT_FOLDER]/webapps/DeSignApp/WEB-INF/classes` folder for the deployed DeSignApp app


#Future features
* Importing wallet file instead of requiring an ethereum private key in the config file
* ERC 725/735 for authentication, authorization and identidy management
* Third party signature request with timeout
* Multi-signature from several authorized parties
* Decentralized proofs of signature (uploaded directly onto IPFS, requires either an IPFS node or the use of filecoins)
* More back-end storage support (local files, non-SQL databases...)