from brownie import DeSign
import json
from hashlib import sha256
from brownie.convert import to_bytes

def main():
	documentFilePath = input("document file path : ")
	documentFile = open(documentFilePath, "rb")
	document = documentFile.read()
	documentHash = sha256(document).digest()
	documentFile.close()

	jsonFilePath = input("sigProof.json file path : ")
	jsonData = json.load(open(jsonFilePath))
	currentStep = documentHash
	for hash in jsonData["merklePath"]:
		hashBytes = bytes.fromhex(hash[2:])
		currentStep = sha256(currentStep + hashBytes).digest()

	contract = DeSign.at(jsonData["contractAddr"])
	r = contract.getIndexData(to_bytes(jsonData["indexHash"]))
	print("Computed merkle root : " + currentStep.hex())
	print("Signed merkle root : " + r[0].hex())