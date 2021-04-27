There are two kinds of tests :
Tests written in python are unit tests for the smart contract only.

Tests written in Java are for the client application.

# Python unit tests

These tests start by setting up some variables in setupVariables.
They also call DeSignContract to deploy a new contract for each individual test.

### testCheckRoles : 
Simply ensures that the OpenZeppelin AccessControl Roles features are correctly implemented. We do not need to test it beyond just making sure it is available in some capacity, assuming the AccessControl contract provided by OpenZeppelin has already been thoroughly tested and audited

### testsignMerkleRoot : 
Will attempt to sign a document volume hash to a given index hash with a given validity time through the signMerkleRoot function.
The first attempt using the owner address should succeed (not raise an exception), while the second one, called from another address, should not, meaning a VirtualMachineError should be raised on the second attempt. The third attempt should also fail, [based on the validity time being 0](https://github.com/guillheu/DeSign/blob/main/design_pattern_decisions.md#guard-check).
Then we attempt to read the newly written data using getIndexData. The retreived data should match the data we originally sent by being equal or slightly lower.
Finally we attempt reading a signature from a non-existant index. That attempt should fail.

### testIntegration :
Full integration test. Contract owner signs a document (with a validity time greater than 0), other signatory can neither sign nor grant themselves roles. Then, contract owner gives other signatory address the signatory role. other signatory can now sign. Signatory renounces his signatory role, and then no longer has the signatory role. Owner still has both signatory and admin roles.



# Java unit tests

These tests are *very* messy. A full refactor is necessary.

### static statement
This initial statement initializes the necessary variables for the tests, such as the config values, the 2 back-end storages, `localStorage` and `SQLStorage` (note : the `TMPLocalFileStorage` class was only used during initial development. Its only current purpose is to validate the tests. It should **NOT** be used for production), as well as the two cores (one for each back-end storage solution)

### testIndexation
Simply attempts to sign a single document, and then checks the hash stored on the blockchain against the original one.

### testIntegration
Runs several instances of [fullCycle](#fullCycle), with both the `SQLStorage` and `localStorage`, each using both `indexVolume1` and `indexVolume2`.

### fullCycle
Technically not a unit test function, but will be called several times by [testIntegration](#testIntegration). Will use a given `DeSignCore` to sign documents indexed to the given `String` and then check the validity of the signature (similar to [testIndexation](#testIndexation)).

### testMySQLStorage
This test was implemented for the first iteration of the SQL storage solution. In order to transition from a local storage to an SQL storage, we simply checked if, **assuming both the local and sql storages contain the same documents for `indexVolume1`**, the Merkle root generated from them should be identical.

### testMerklePathGenerator
This was implemented for the first iteration of the proof of signature, which requires a valid Merkle path. This test will *not* use pre-existing storages, and instead will create an entirely separate Merkle tree. Then, the Merkle path is generated at `merklePath = merkleTree.getMerklePath(sigs.get(0));`. Then, we *manually* create the hashes that *should* be contained in said Merkle path. Finally, we check that those hashes match those of the Merkle path and that the Merkle roots are identical.

### testGenerateSignatureProof
This test will start by signing the document volume 1 (to avoid errors when redeploying the contract), then will generate a signature proof in `SignatureProof sigProof = coreSQLDB.getSignatureProof(document, nodeURL);` for the `document` (which contains bytes for what is expected to be document 1, indexed at indexVolume1). Then, we manually follow the Merkle path of the proof of signature, and check the found Merkle root against the one which was expected from the storage. Finally, we build a new instance of `Web3j` using the node URL given in the proof of signature, of `DeSign` - the contract wrapper - using the contract address given in the proof, and call that contract, attempt to fetch the signature at the index given in the proof of signature, and check the signed hash against the Merkle root found earlier.

### testRoles
Simple test that ensures role management through `DeSignCore` and the wrapper function work and register correctly.