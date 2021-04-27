This project has very little solidity code. The tiny attack surface makes it rather easy to audit and secure.

The majority of attacks rely on some form of interaction between a smart contract and an outside agent, that being through a Ether transfer or an external call, which this project does not use at this time.

Other attacks that would use the contract's inner logic to generate, for instance, a DoS, are easily avoided through the sheer simplicity of the solidity code.

Mythril and Slither auditing have reported 3 security concerns :
	- OpenZeppelin's libraries use solidity version 0.8.0, while our smart contract uses version 0.8.3
	- Slither recommends not using a solc version later than 0.6.7 for deployment
	- The use of block.timestamp means a miner could manipulate the validity of the stored signatures by up to 15 seconds, both when reading and writing them.

We consider those concerns to be minor and the current state of the solidity code to be acceptable for deployment.

### A note on the stored indices
The DeSign smart contract's signature storage is vulnerable to rainbow table attacks (brute forcing through a list of pre-hashed indices) of the indices. However we consider this vulnerability to not be significant ([Yet, until we implement an ERC 725](https://github.com/guillheu/DeSign#future-features)), and guarding against it (usage of a list instead of a mapping, generating and saving random salt values) would significantly increase deployment and usage costs.