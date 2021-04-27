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
	global signatory
	signatory = accounts[1]
	global indexHash 
	indexHash = to_bytes(hashlib.sha256("01/01/1980".encode()).hexdigest())
	global merkleRoot 
	merkleRoot = to_bytes(hashlib.sha256("THIS IS A BLOCK OF DOCUMENTS".encode()).hexdigest())
	global indexHash2
	indexHash2 = to_bytes(hashlib.sha256("01/01/2000".encode()).hexdigest())
	global merkleRoot2 
	merkleRoot2 = to_bytes(hashlib.sha256("This is fine.".encode()).hexdigest())
	global validityTime
	validityTime = 365
	global signatoryRole
	signatoryRole = "0x9838a05512653d899e198165cc2a8305c24ac29892037f9cab63bdb153121845" # keccak256 of "SIGNATORY_ROLE"



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

	try:
		DeSignContract.signMerkleRoot(indexHash, merkleRoot, 0, {"from":owner})
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
	assert indexData[2] == owner

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


	###################################
	#########integration tests#########
	###################################

def testIntegration(DeSignContract):
	""" scenario : contract owner signs a document, other signatory cannot either sign nor change roles
	then, contract owner gives other signatory address the signatory role. other signatory can now sign
	signatory renounces his signatory role, and then cannot sign anymore.
	owner can still sign."""

	print("index hash : " + indexHash.hex())
	print("merkle root / document block hash : " + merkleRoot.hex())
	#Owner signs a document
	DeSignContract.signMerkleRoot(indexHash, merkleRoot, validityTime, {"from":owner})
	try:
		#other signatory (not yet authorized) attemps to sign a document and fails
		DeSignContract.signMerkleRoot(indexHash, merkleRoot, validityTime, {"from":accounts[1]})
		assert False
	except(VirtualMachineError):
		assert True
	else:
		assert False

		#Additional attempt from the owner, with a validity time of 0 ; should fail
	try:
		DeSignContract.signMerkleRoot(indexHash, merkleRoot, 0, {"from":owner})
		assert False
	except(VirtualMachineError):
		assert True
	else:
		assert False


	#Checking the signature...
	indexData = DeSignContract.getIndexData(indexHash)
	print(indexData)
	assert indexData[0] == "0x" + merkleRoot.hex()
	assert indexData[1] <= validityTime
	assert indexData[2] == owner
	#Making sure we did not accidentally set all the indices
	try:
		indexData = DeSignContract.getIndexData(to_bytes(hashlib.sha256("an unused index".encode()).hexdigest()))
	except(VirtualMachineError):
		assert True
	else:
		assert False


	#Granting new signatory authorization

	#First, new signatory cannot grant themselves a new role
	try:
		DeSignContract.grantRole(signatoryRole, signatory, {"from": signatory})
	except(VirtualMachineError):
		assert True
	else:
		assert False

	#Second, owner grants signatory their role

	DeSignContract.grantRole(signatoryRole, signatory, {"from": owner})

	#Signatory still cannot grant roles
	try:
		DeSignContract.grantRole(signatoryRole, accounts[2], {"from": signatory})
	except(VirtualMachineError):
		assert True
	else:
		assert False


	#Signatory signs document
	DeSignContract.signMerkleRoot(indexHash2, merkleRoot2, validityTime, {"from":signatory})

	#Checking the signature...
	indexData2 = DeSignContract.getIndexData(indexHash2)
	print(indexData2)
	assert indexData2[0] == "0x" + merkleRoot2.hex()
	assert indexData2[1] <= validityTime
	assert indexData2[2] == signatory
	#Making sure we did not accidentally set all the indices
	try:
		indexData = DeSignContract.getIndexData(to_bytes(hashlib.sha256("an unused index".encode()).hexdigest()))
	except(VirtualMachineError):
		assert True
	else:
		assert False


	#Signatory renounces role, should no longer have the signatory role

	DeSignContract.renounceRole(signatoryRole, signatory, {"from": signatory})
	isSignatory = DeSignContract.hasRole(signatoryRole, signatory)
	assert not isSignatory

	#Owner should still be both DEFAULT_ADMIN & SIGNATORY
	isAdmin = DeSignContract.hasRole("0x00", owner)
	isSignatory = DeSignContract.hasRole(signatoryRole, owner)
	assert isAdmin and isSignatory