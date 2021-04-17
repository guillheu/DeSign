import os

def main():
	contractFile = open("contracts/DeSign.sol")
	contractContent = contractFile.read()
	newContractContent = contractContent.replace("OpenZeppelin/openzeppelin-contracts@4.0.0", "./import/openzeppelin-contracts-4.0.0", 1)
	newContract = open("../DeSign-java/contracts/DeSign.sol", "w")
	newContract.write(newContractContent)
	contractFile.close()
	newContract.close()
	os.chdir("../DeSign-java")
	os.system("./solc-static-linux ./contracts/DeSign.sol --bin --abi --optimize -o ./bin/ --overwrite")
	os.system("web3j generate solidity -a=./bin/DeSign.abi -b=./bin/DeSign.bin -o=./Eclipse-workspace/DeSignApp/src/main/java -p=contractWrappers")

	



	