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
	for step in jsonData["merklePath"]:
		hash = step["hashWith"]
		hashBytes = bytes.fromhex(hash[2:])
		if(step["hashFrom"] == "LEFT"):
			currentStep = sha256(currentStep + hashBytes).digest()
		elif(step["hashFrom"] == "RIGHT"):
			currentStep = sha256(hashBytes + currentStep).digest()
		else:
			raise Exception("Invalid merkle path : unknown value for hashFrom : \"" + step["hashFrom"] + "\"")

	contract = DeSign.at(jsonData["contractAddr"])
	r = contract.getIndexData(to_bytes(jsonData["indexHash"]))
	print("Computed merkle root : \t" + currentStep.hex())
	print("Signed merkle root : \t" + r[0].hex())
	if(currentStep.hex() == r[0].hex()):
		print("This document was properly signed!")