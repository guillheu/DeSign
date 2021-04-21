//SPDX-License-Identifier: UNLICENSED

pragma solidity 0.8.3;

//The AccessControl scheme should eventually be replaced by the ERC725
//Also, look into ERC734 for key & signature management, and ERC735 (might not be necessary) for identity
import "OpenZeppelin/openzeppelin-contracts@4.0.0/contracts/access/AccessControl.sol";


/** 
 * @title Decentralized Signature 
 * @author Guillaume Heu
 * @notice This is a simple implementation of a document digital signature scheme
 * @dev This contract should be deployed by the client who needs to sign documents stored by other means. This only stores signatures.
 * @custom:work-in-progress This contract is not finalized and should not be used in production environments
 */ 

contract DeSign is AccessControl{

/**
 * @notice Logs all signatures with the address of the signatory, the hashed index, and the rest of the data contained in {IndexEntry}
 */
	event SignedEntry(bytes32 indexed indexHash, bytes32 indexed _documentVolumeHash, address indexed _signatory);

    bytes32 public constant SIGNATORY_ROLE = keccak256("SIGNATORY_ROLE");

	struct IndexEntry {
		bytes32 _documentVolumeHash;
		uint _expirationTimestamp;
		address _signatory;
	}

	mapping (bytes32 => IndexEntry) private index;

/** 
 * @notice Sets the caller as administrator and signatory.
 * @dev Roles are implemented using OpenZeppelin's AccessControl.sol contract
 * @custom:work-in-progress Role management should eventually be managed through an ERC 725
 */
    constructor() {
    	_setupRole(DEFAULT_ADMIN_ROLE, msg.sender);		//TODO : refactor for ERC 725
    	_setupRole(SIGNATORY_ROLE, msg.sender);			//TODO : refactor for ERC 725
    }




/**
 * @notice Functions that implement this modifier can only be called by someone with a signatory role
 * @custom:work-in-progress This will likely be changed or scrapped to implement an ERC725 contract
 */
	modifier onlySignatory{

		require(hasRole(SIGNATORY_ROLE, msg.sender), "MUST BE SIGNATORY");
		_;
	}

/**
 * @notice indexes a fingerprint to a given index and implicitly considers it a signature by the caller. The caller must have the signatory role. This function is index agnostic ; the index can be anything, like a date, a location, a person. The fingerprint and index are identifyable, but not readable : even if they're derived from sensitive information, that information is never shared with the blockchain, and they cannot be reverted to their original content.
 * @dev The index hashing algorithm has to remain consistent accross all signatures ; there is currently no on-chain check for the hashing algorithm used. You are responsible for the hashing of your own signatures. Emits a {SignedEntry} event.
 * @param _indexHash A 32 bytes long hash of whatever index is used to identify a document volume. It has to match the index used to identify the document volume in the back-end storage system of the client.
 * @param documentVolumeHash A 32 bytes long hash that can be systematically generated from any document or set of documents, for example using a Merkle tree root.
 * @param _secondsBeforeExpiration The amount of seconds before the signature is considered invalid. This is to let users know that beyond the expiration date, the signatory is no longer committed to certifying the documents or even to keep them in their back-end storage.
 */
	function signMerkleRoot(bytes32 _indexHash, bytes32 documentVolumeHash, uint _secondsBeforeExpiration) external onlySignatory {
		index[_indexHash] = IndexEntry(documentVolumeHash, block.timestamp + _secondsBeforeExpiration, msg.sender);
		emit SignedEntry(_indexHash, documentVolumeHash, msg.sender);
	}

/**
 * @notice retreives the data stored during the signature process called by {signMerkleRoot}.
 * @param _indexHash A 32 bytes long hash of whatever index is used to identify a document volume. It has to match the index used to identify the document volume in the back-end storage system of the client. If no signature matches this index, then the call will revert.
 * @return A 32 bytes long hash that can be systematically generated from any document or set of documents, for example using a Merkle tree root.
 * @return The lifetime of the signature in seconds. If a negative value, the signature is no longer valid.
 */

	function getIndexData(bytes32 _indexHash) external view returns (bytes32, uint, address){
		IndexEntry memory entry = index[_indexHash];
		return (entry._documentVolumeHash, entry._expirationTimestamp - block.timestamp, entry._signatory);
	}
}