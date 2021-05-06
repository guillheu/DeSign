package web;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

public class WebHelper {
	public static String printTransaction(TransactionReceipt receipt, int chainID) {
		String r = "";
		if(chainID == 1)
			r += "<a href='https://etherscan.io/tx/"+ receipt.getTransactionHash() +"' target='_blank'>View transaction on Etherscan</a>"+ "<br>";
		if(chainID == 3)
			r += "<a href='https://ropsten.etherscan.io/tx/"+ receipt.getTransactionHash() +"' target='_blank'>View transaction on Etherscan (ropsten testnet)</a>"+ "<br>";
		if(chainID == 42)
			r += "<a href='https://kovan.etherscan.io/tx/"+ receipt.getTransactionHash() +"' target='_blank'>View transaction on Etherscan</a> (kovan testnet)"+ "<br>";
		if(chainID == 4)
			r += "<a href='https://rinkeby.etherscan.io/tx/"+ receipt.getTransactionHash() +"' target='_blank'>View transaction on Etherscan</a> (rinkeby testnet)"+ "<br>";
		if(chainID == 420)
			r += "<a href='https://goerli.etherscan.io/tx/"+ receipt.getTransactionHash() +"' target='_blank'>View transaction on Etherscan</a> (goerli testnet)"+ "<br>";
		r += "Transaction hash : "+receipt.getTransactionHash()+ "<br>";
		r += "Block number : "+receipt.getBlockNumber() + "<br>";
		r += "Gas used : "+receipt.getCumulativeGasUsed()+ "<br><br>";
		return r;
	}
}
