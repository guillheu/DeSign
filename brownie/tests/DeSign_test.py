from brownie import accounts, DeSign
from brownie.convert import to_bytes
from brownie.exceptions import *
import brownie
import pytest
import hashlib

@pytest.fixture(autouse=True)
def setupVariables():
	global owner
	owner = accounts[0]
	global indexHash 
	indexHash = to_bytes(hashlib.sha256("01/01/1980".encode()).hexdigest())
	global merkleRoot 
	merkleRoot = to_bytes(hashlib.sha256("THIS IS A BLOCK OF DOCUMENTS".encode()).hexdigest())
	global validityTime
	validityTime = 365
	global documentBlockLink
	documentBlockLink = "SQL SIGMA BALLS"


@pytest.fixture
def DeSignContract():
	return DeSign.deploy({'from':owner})

def testCheckOwner(DeSignContract):
	assert DeSignContract.owner() == owner

def testIndexMerkleRoot(DeSignContract):
	print("index hash : " + indexHash.hex())
	print("merkle root / document block hash : " + merkleRoot.hex())
	DeSignContract.indexMerkleRoot(indexHash, merkleRoot, bytes(documentBlockLink, 'utf-8'), validityTime, {"from":owner})
	try:
		DeSignContract.indexMerkleRoot(indexHash, merkleRoot, bytes(documentBlockLink, 'utf-8'), validityTime, {"from":accounts[1]})
		assert False
	except(VirtualMachineError):
		assert True
	else:
		assert False


	########################################################
	####test merkle root & document block link retreival####
	########################################################

	indexData = DeSignContract.getIndexData(indexHash)
	print(indexData)
	assert indexData[0] == "0x" + merkleRoot.hex()
	assert indexData[1].decode('utf-8') == documentBlockLink
	assert indexData[2] <= validityTime * 86400