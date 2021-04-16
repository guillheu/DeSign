pragma solidity ^0.8.0;

//The AccessControl scheme should eventually be replaced by the ERC725
//Also, look into ERC734 for key & signature management, and ERC735 (might not be necessary) for identity
import "OpenZeppelin/openzeppelin-contracts@4.0.0/contracts/access/AccessControl.sol";

contract DeSign is AccessControl{

    bytes32 public constant SIGNATORY_ROLE = keccak256("SIGNATORY_ROLE");


    constructor() {
    	_setupRole(DEFAULT_ADMIN_ROLE, msg.sender);		//TODO : refactor for ERC 725
    	_setupRole(SIGNATORY_ROLE, msg.sender);			//TODO : refactor for ERC 725
    }

	event SignedEntry(address indexed signatory, bytes32 indexed indexHash, IndexEntry indexed indexEntry);

	struct IndexEntry {
		bytes32 _documentVolumeHash;
		uint _expirationTimestamp;
	}

	mapping (bytes32 => IndexEntry) private index;


	//TODO : refactor for ERC 725
	modifier onlySignatory{

		require(hasRole(SIGNATORY_ROLE, msg.sender));
		_;
	}



	function signMerkleRoot(bytes32 _indexHash, bytes32 documentVolumeHash, uint _daysBeforeExpiration) public onlySignatory {
		index[_indexHash] = IndexEntry(documentVolumeHash, block.timestamp + (_daysBeforeExpiration * 86400));
		emit SignedEntry(msg.sender, _indexHash, index[_indexHash]);
	}

	function getIndexData(bytes32 _indexHash) public view returns (bytes32, uint){
		IndexEntry memory entry = index[_indexHash];
		return (entry._documentVolumeHash, entry._expirationTimestamp - block.timestamp);
	}
}