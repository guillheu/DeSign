pragma solidity ^0.8.0;


import "OpenZeppelin/openzeppelin-contracts@4.0.0/contracts/access/Ownable.sol";

contract DeSign is Ownable{

	struct IndexEntry {
		bytes32 _documentBlockHash;
		bytes _documentBlockLink;
		uint _expirationTimestamp;
	}

	mapping (bytes32 => IndexEntry) private index;

	function indexMerkleRoot(bytes32 _indexHash, bytes32 documentBlockHash, bytes memory documentBlockLink, uint _daysBeforeExpiration) public onlyOwner {
		index[_indexHash] = IndexEntry(documentBlockHash, documentBlockLink, block.timestamp + (_daysBeforeExpiration * 86400));
	}

	function getIndexData(bytes32 _indexHash) public view returns (bytes32, bytes memory, uint){
		IndexEntry memory entry = index[_indexHash];
		return (entry._documentBlockHash, entry._documentBlockLink, entry._expirationTimestamp - block.timestamp);
	}
}