from brownie import accounts, DeSign

def main():
	
	#deploying to blockchain

	accounts.load("dev_kovan")
	contract = DeSign.deploy({'from': accounts[0]})
	#djangoDeploy("Defi", contract)