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


@pytest.fixture
def DeSignContract():
	return DeSign.deploy({'from':owner})

def testCheckRoles(DeSignContract):
	assert DeSignContract.hasRole(to_bytes(0x00), owner)

def testsignMerkleRoot(DeSignContract):
	print("index hash : " + indexHash.hex())
	print("merkle root / document block hash : " + merkleRoot.hex())
	signingTransaction = DeSignContract.signMerkleRoot(indexHash, merkleRoot, validityTime, {"from":owner})
	try:
		DeSignContract.signMerkleRoot(indexHash, merkleRoot, validityTime, {"from":accounts[1]})
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
	assert indexData[1] <= validityTime

	try:
		indexData = DeSignContract.getIndexData(to_bytes(hashlib.sha256("an unused index".encode()).hexdigest()))
	except(VirtualMachineError):
		assert True
	else:
		assert False


	###################################
	####test signMerkleRoot events ####
	###################################
	events = signingTransaction.events
	print("HERE COMES THE EVENT : ")
	print(events['SignedEntry']["indexHash"])
	assert events['SignedEntry']["indexHash"] == "0x" + indexHash.hex()
	assert events['SignedEntry']["_documentVolumeHash"] == "0x" + merkleRoot.hex()
	assert events['SignedEntry']["_signatory"] == owner