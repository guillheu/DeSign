# Design pattern decisions

These patterns ensure the solidity smart contracts are coherent with common good practices.
[See this for more details](https://fravoll.github.io/solidity-patterns/)

The DeSign project currently only works on a single smart contract ; any further mention of "the contract" refers to the [DeSign.sol smart contract](https://github.com/guillheu/DeSign/blob/main/brownie/contracts/DeSign.sol)

# Behavioral patterns

### Guard check
[Applicability : "you want to validate user inputs."](https://fravoll.github.io/solidity-patterns/guard_check.html)

When calling `signMerkleRoot`, the hashes of the index & document volume are considered by the smart as fully random, and therefor no check can be operated. However, a validity time of 0 leads to an immediately invalid signature, and therefor should not be permitted. 

The use of a `require(...)` check statement at the beginning of the function ensures minimal gas loss for the caller.

### Access restriction
[Applicability : "you want to increase security of your smart contract against unauthorized access."](https://fravoll.github.io/solidity-patterns/access_restriction.html)

The only condition that needs to be fulfilled for the contract to operate safely is the authentication of the person signing the contract as a signatory ([See OpenZeppelin's AccessControl.sol](https://github.com/OpenZeppelin/openzeppelin-contracts/blob/master/contracts/access/AccessControl.sol)). The `onlySignatory` modifier applied to the `signMerkleRoot` function ensures that condition remains true. 

The use of a `require()` at the beginning of the function ensures minimal gas loss.

### Tight variable packing
[Applicability : "you are using a struct consisting of more than one variable and can afford to use variables of smaller sizes."](https://fravoll.github.io/solidity-patterns/tight_variable_packing.html)

This design pattern was, in fact, *not* implemented, despite its apparent applicability.

It was considered to use a `uint64` for the storage of the `_expirationTimestamp` value, instead of a `uint256`, since that would allow timestamp values up to 1.844674407×10¹⁹, which corresponds to 585 billion years (roughly 45 times the estimated age of the observable universe), more than enough for our signature storage needs.

However, the EVM still allocates memory by chunks of 32 bytes (256 bits). Usually, we would take advantage of that fact by packing several `uint64`s together (declaring them successively). However, since we would only be using a single `uint64`, there would not be any actual memory gains.

Not only that, but since `block.timestamp` is a `uint256`, doing calculations between our stored timestamp and the `block.timestamp` value requires us to cast `block.timestamp` as a `uint64` beforehand, consequently increasing gas costs.

It was therefor **cheaper** to keep `_expirationTimestamp` as a `uint256` instead of a `uint64`. However that may change if the structure `IndexEntry` needs more values smaller than 32 bytes.