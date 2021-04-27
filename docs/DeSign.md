## `DeSign`

This is a simple implementation of a document digital signature scheme


This contract should be deployed by the client who needs to sign documents stored by other means. This only stores signatures.
@custom:work-in-progress This contract is not finalized and should not be used in production environments

### `onlySignatory()`

Functions that implement this modifier can only be called by someone with a signatory role
@custom:work-in-progress This will likely be changed or scrapped to implement an ERC725 contract




### `constructor()` (public)

Sets the caller as administrator and signatory.


Roles are implemented using OpenZeppelin's AccessControl.sol contract
@custom:work-in-progress Role management should eventually be managed through an ERC 725

### `signMerkleRoot(bytes32 _indexHash, bytes32 documentVolumeHash, uint256 _secondsBeforeExpiration)` (external)

indexes a fingerprint to a given index and implicitly considers it a signature by the caller. The caller must have the signatory role. This function is index agnostic ; the index can be anything, like a date, a location, a person. The fingerprint and index are identifyable, but not readable : even if they're derived from sensitive information, that information is never shared with the blockchain, and they cannot be reverted to their original content.


The index hashing algorithm has to remain consistent accross all signatures ; there is currently no on-chain check for the hashing algorithm used. You are responsible for the hashing of your own signatures. Emits a {SignedEntry} event.


### `getIndexData(bytes32 _indexHash) → bytes32, uint256` (external)

retreives the data stored during the signature process called by {signMerkleRoot}.





### `SignedEntry(address signatory, bytes32 indexHash, struct DeSign.IndexEntry indexEntry)`

Logs all signatures with the address of the signatory, the hashed index, and the rest of the data contained in {IndexEntry}



