There are two kinds of tests :
Tests written in python are unit tests for the smart contract only.

Tests written in Java are for the client application.

# Python unit tests

These tests start by setting up some variables in setupVariables.
They also call DeSignContract to deploy a new contract for each individual test.

testCheckRoles : 
Simply ensures that the OpenZeppelin AccessControl Roles features are correctly implemented. We do not need to test it beyond just making sure it is available in some capacity.

testsignMerkleRoot : 
Will attempt to sign a document volume hash to a given index hash with a given validity time through the signMerkleRoot function.
The first attempt using the owner address should succeed (not raise an exception), while the second one, called from another address, should not, meaning a VirtualMachineError should be raised on the second attempt.
Then we attempt to read the newly written data using getIndexData. The retreived data should match the data we originally sent by being equal or slightly lower.
Finally we attempt reading a signature from a non-existant index. That attempt should fail.

# Java unit tests

Coming soon