from brownie import accounts, DeSign

def main():
	
	#deploying to blockchain

	accounts.default = accounts[0];
	contract = DeSign.deploy({'from': accounts[0]})
	#djangoDeploy("Defi", contract)