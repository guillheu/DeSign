import os, shutil

def main():
	contractFile = open("contracts/DeSign.sol")
	contractContent = contractFile.read()
	newContractContent = contractContent.replace("OpenZeppelin/openzeppelin-contracts@4.0.0", "./import/openzeppelin-contracts-4.0.0", 1)
	newContract = open("../DeSign-java/contracts/DeSign.sol", "w")
	newContract.write(newContractContent)
	contractFile.close()
	newContract.close()
	os.chdir("../DeSign-java")
	for filename in os.listdir("bin"):
		file_path = os.path.join("bin", filename)
	try:
		if os.path.isfile(file_path) or os.path.islink(file_path):
			os.unlink(file_path)
		elif os.path.isdir(file_path):
			shutil.rmtree(file_path)
	except Exception as e:
		print('Failed to delete %s. Reason: %s' % (file_path, e))
	os.system("./solc-static-linux ./contracts/DeSign.sol --bin --abi --optimize -o ./bin/")
	os.system("web3j generate solidity -a=./bin/DeSign.abi -b=./bin/DeSign.bin -o=./Eclipse-workspace/DeSignApp/src/main/java -p=contractWrappers")

	



	