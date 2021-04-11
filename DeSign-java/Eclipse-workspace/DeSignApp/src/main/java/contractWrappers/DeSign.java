package contractWrappers;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class DeSign extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b5061001c60003361004b565b6100467f9838a05512653d899e198165cc2a8305c24ac29892037f9cab63bdb1531218453361004b565b6100f7565b6100558282610059565b5050565b6000828152602081815260408083206001600160a01b038516845290915290205460ff16610055576000828152602081815260408083206001600160a01b03851684529091529020805460ff191660011790556100b33390565b6001600160a01b0316816001600160a01b0316837f2f8788117e7eff1d82e926ec794901d17c78024a50270940304540a733656f0d60405160405180910390a45050565b610a4b806101066000396000f3fe608060405234801561001057600080fd5b506004361061009e5760003560e01c80637774d523116100665780637774d5231461013757806391d1485414610159578063a217fddf1461016c578063adad536714610174578063d547741f1461019b5761009e565b806301ffc9a7146100a3578063248a9ca3146100cb5780632f2ff15d146100fc57806336568abe14610111578063370a4cf614610124575b600080fd5b6100b66100b136600461081b565b6101ae565b60405190151581526020015b60405180910390f35b6100ee6100d9366004610704565b60009081526020819052604090206001015490565b6040519081526020016100c2565b61010f61010a36600461071c565b6101e5565b005b61010f61011f36600461071c565b610279565b61010f610132366004610756565b6102f3565b61014a610145366004610704565b6103e4565b6040516100c2939291906108fd565b6100b661016736600461071c565b6104d9565b6100ee600081565b6100ee7f9838a05512653d899e198165cc2a8305c24ac29892037f9cab63bdb15312184581565b61010f6101a936600461071c565b610502565b60006001600160e01b03198216637965db0b60e01b14806101df57506301ffc9a760e01b6001600160e01b03198316145b92915050565b600082815260208190526040902060010154610202905b33610167565b61026b5760405162461bcd60e51b815260206004820152602f60248201527f416363657373436f6e74726f6c3a2073656e646572206d75737420626520616e60448201526e0818591b5a5b881d1bc819dc985b9d608a1b60648201526084015b60405180910390fd5b6102758282610582565b5050565b6001600160a01b03811633146102e95760405162461bcd60e51b815260206004820152602f60248201527f416363657373436f6e74726f6c3a2063616e206f6e6c792072656e6f756e636560448201526e103937b632b9903337b91039b2b63360891b6064820152608401610262565b6102758282610606565b61031d7f9838a05512653d899e198165cc2a8305c24ac29892037f9cab63bdb153121845336104d9565b61032657600080fd5b6040518060600160405280848152602001838152602001826201518061034c9190610978565b6103569042610960565b905260008581526001602081815260409092208351815583830151805191936103849385019291019061066b565b506040918201516002909101556000858152600160205281902090516103aa919061084a565b60405190819003812090859033907f8c5f4bbc5806df0dce268c7d9e8ae4d5d2d58c20df55195a77015f94947d6afb90600090a450505050565b600060606000806001600086815260200190815260200160002060405180606001604052908160008201548152602001600182018054610423906109ae565b80601f016020809104026020016040519081016040528092919081815260200182805461044f906109ae565b801561049c5780601f106104715761010080835404028352916020019161049c565b820191906000526020600020905b81548152906001019060200180831161047f57829003601f168201915b505050505081526020016002820154815250509050806000015181602001514283604001516104cb9190610997565b935093509350509193909250565b6000918252602082815260408084206001600160a01b0393909316845291905290205460ff1690565b60008281526020819052604090206001015461051d906101fc565b6102e95760405162461bcd60e51b815260206004820152603060248201527f416363657373436f6e74726f6c3a2073656e646572206d75737420626520616e60448201526f2061646d696e20746f207265766f6b6560801b6064820152608401610262565b61058c82826104d9565b610275576000828152602081815260408083206001600160a01b03851684529091529020805460ff191660011790556105c23390565b6001600160a01b0316816001600160a01b0316837f2f8788117e7eff1d82e926ec794901d17c78024a50270940304540a733656f0d60405160405180910390a45050565b61061082826104d9565b15610275576000828152602081815260408083206001600160a01b0385168085529252808320805460ff1916905551339285917ff6391f5c32d9c69d2a47ea670b442974b53935d1edc7fd64eb21e047a839171b9190a45050565b828054610677906109ae565b90600052602060002090601f01602090048101928261069957600085556106df565b82601f106106b257805160ff19168380011785556106df565b828001600101855582156106df579182015b828111156106df5782518255916020019190600101906106c4565b506106eb9291506106ef565b5090565b5b808211156106eb57600081556001016106f0565b600060208284031215610715578081fd5b5035919050565b6000806040838503121561072e578081fd5b8235915060208301356001600160a01b038116811461074b578182fd5b809150509250929050565b6000806000806080858703121561076b578182fd5b8435935060208501359250604085013567ffffffffffffffff80821115610790578384fd5b818701915087601f8301126107a3578384fd5b8135818111156107b5576107b56109ff565b604051601f8201601f19908116603f011681019083821181831017156107dd576107dd6109ff565b816040528281528a60208487010111156107f5578687fd5b826020860160208301379182016020019590955295989497509495606001359450505050565b60006020828403121561082c578081fd5b81356001600160e01b031981168114610843578182fd5b9392505050565b600082548252602060018085018381548581851c90508482168061086f57607f821691505b86821081141561088d57634e487b7160e01b88526022600452602488fd5b8080156108a157600181146108b5576108e3565b60ff1984168a89015260408a0194506108e3565b600086815260209020895b848110156108db5781548c82018b01529088019089016108c0565b8b0189019550505b505050506002870154815260208101979650505050505050565b600084825260206060818401528451806060850152825b8181101561093057868101830151858201608001528201610914565b818111156109415783608083870101525b5060408401949094525050601f91909101601f19160160800192915050565b60008219821115610973576109736109e9565b500190565b6000816000190483118215151615610992576109926109e9565b500290565b6000828210156109a9576109a96109e9565b500390565b600181811c908216806109c257607f821691505b602082108114156109e357634e487b7160e01b600052602260045260246000fd5b50919050565b634e487b7160e01b600052601160045260246000fd5b634e487b7160e01b600052604160045260246000fdfea2646970667358221220d374fc57d51f2112b4e61701f7af2cdbb9d3c19b65e6b2d6e8ed9e502fcb766664736f6c63430008030033";

    public static final String FUNC_DEFAULT_ADMIN_ROLE = "DEFAULT_ADMIN_ROLE";

    public static final String FUNC_SIGNATORY_ROLE = "SIGNATORY_ROLE";

    public static final String FUNC_GETINDEXDATA = "getIndexData";

    public static final String FUNC_GETROLEADMIN = "getRoleAdmin";

    public static final String FUNC_GRANTROLE = "grantRole";

    public static final String FUNC_HASROLE = "hasRole";

    public static final String FUNC_RENOUNCEROLE = "renounceRole";

    public static final String FUNC_REVOKEROLE = "revokeRole";

    public static final String FUNC_SIGNMERKLEROOT = "signMerkleRoot";

    public static final String FUNC_SUPPORTSINTERFACE = "supportsInterface";

    public static final Event ROLEADMINCHANGED_EVENT = new Event("RoleAdminChanged", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<Bytes32>(true) {}));
    ;

    public static final Event ROLEGRANTED_EVENT = new Event("RoleGranted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event ROLEREVOKED_EVENT = new Event("RoleRevoked", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event SIGNEDENTRY_EVENT = new Event("SignedEntry", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Bytes32>(true) {}, new TypeReference<IndexEntry>(true) {}));
    ;

    @Deprecated
    protected DeSign(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DeSign(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DeSign(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DeSign(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<RoleAdminChangedEventResponse> getRoleAdminChangedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ROLEADMINCHANGED_EVENT, transactionReceipt);
        ArrayList<RoleAdminChangedEventResponse> responses = new ArrayList<RoleAdminChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoleAdminChangedEventResponse typedResponse = new RoleAdminChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.previousAdminRole = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.newAdminRole = (byte[]) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RoleAdminChangedEventResponse> roleAdminChangedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RoleAdminChangedEventResponse>() {
            @Override
            public RoleAdminChangedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ROLEADMINCHANGED_EVENT, log);
                RoleAdminChangedEventResponse typedResponse = new RoleAdminChangedEventResponse();
                typedResponse.log = log;
                typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.previousAdminRole = (byte[]) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.newAdminRole = (byte[]) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RoleAdminChangedEventResponse> roleAdminChangedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROLEADMINCHANGED_EVENT));
        return roleAdminChangedEventFlowable(filter);
    }

    public List<RoleGrantedEventResponse> getRoleGrantedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ROLEGRANTED_EVENT, transactionReceipt);
        ArrayList<RoleGrantedEventResponse> responses = new ArrayList<RoleGrantedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoleGrantedEventResponse typedResponse = new RoleGrantedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RoleGrantedEventResponse> roleGrantedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RoleGrantedEventResponse>() {
            @Override
            public RoleGrantedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ROLEGRANTED_EVENT, log);
                RoleGrantedEventResponse typedResponse = new RoleGrantedEventResponse();
                typedResponse.log = log;
                typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RoleGrantedEventResponse> roleGrantedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROLEGRANTED_EVENT));
        return roleGrantedEventFlowable(filter);
    }

    public List<RoleRevokedEventResponse> getRoleRevokedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ROLEREVOKED_EVENT, transactionReceipt);
        ArrayList<RoleRevokedEventResponse> responses = new ArrayList<RoleRevokedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoleRevokedEventResponse typedResponse = new RoleRevokedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<RoleRevokedEventResponse> roleRevokedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, RoleRevokedEventResponse>() {
            @Override
            public RoleRevokedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ROLEREVOKED_EVENT, log);
                RoleRevokedEventResponse typedResponse = new RoleRevokedEventResponse();
                typedResponse.log = log;
                typedResponse.role = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.account = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.sender = (String) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<RoleRevokedEventResponse> roleRevokedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROLEREVOKED_EVENT));
        return roleRevokedEventFlowable(filter);
    }

    public List<SignedEntryEventResponse> getSignedEntryEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(SIGNEDENTRY_EVENT, transactionReceipt);
        ArrayList<SignedEntryEventResponse> responses = new ArrayList<SignedEntryEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SignedEntryEventResponse typedResponse = new SignedEntryEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.signatory = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.indexHash = (byte[]) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.indexEntry = (IndexEntry) eventValues.getIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SignedEntryEventResponse> signedEntryEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SignedEntryEventResponse>() {
            @Override
            public SignedEntryEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SIGNEDENTRY_EVENT, log);
                SignedEntryEventResponse typedResponse = new SignedEntryEventResponse();
                typedResponse.log = log;
                typedResponse.signatory = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.indexHash = (byte[]) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.indexEntry = (IndexEntry) eventValues.getIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Flowable<SignedEntryEventResponse> signedEntryEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SIGNEDENTRY_EVENT));
        return signedEntryEventFlowable(filter);
    }

    public RemoteFunctionCall<byte[]> DEFAULT_ADMIN_ROLE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DEFAULT_ADMIN_ROLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<byte[]> SIGNATORY_ROLE() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SIGNATORY_ROLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<Tuple3<byte[], byte[], BigInteger>> getIndexData(byte[] _indexHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETINDEXDATA, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_indexHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<DynamicBytes>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple3<byte[], byte[], BigInteger>>(function,
                new Callable<Tuple3<byte[], byte[], BigInteger>>() {
                    @Override
                    public Tuple3<byte[], byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<byte[], byte[], BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<byte[]> getRoleAdmin(byte[] role) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETROLEADMIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> grantRole(byte[] role, String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GRANTROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> hasRole(byte[] role, String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_HASROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceRole(byte[] role, String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeRole(byte[] role, String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REVOKEROLE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(role), 
                new org.web3j.abi.datatypes.Address(160, account)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> signMerkleRoot(byte[] _indexHash, byte[] documentVolumeHash, byte[] documentBlockLink, BigInteger _daysBeforeExpiration) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SIGNMERKLEROOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_indexHash), 
                new org.web3j.abi.datatypes.generated.Bytes32(documentVolumeHash), 
                new org.web3j.abi.datatypes.DynamicBytes(documentBlockLink), 
                new org.web3j.abi.datatypes.generated.Uint256(_daysBeforeExpiration)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> supportsInterface(byte[] interfaceId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SUPPORTSINTERFACE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes4(interfaceId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    @Deprecated
    public static DeSign load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DeSign(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DeSign load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DeSign(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DeSign load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DeSign(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DeSign load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DeSign(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<DeSign> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DeSign.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<DeSign> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DeSign.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DeSign> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DeSign.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DeSign> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DeSign.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class IndexEntry extends DynamicStruct {
        public byte[] _documentVolumeHash;

        public byte[] _documentBlockLink;

        public BigInteger _expirationTimestamp;

        public IndexEntry(byte[] _documentVolumeHash, byte[] _documentBlockLink, BigInteger _expirationTimestamp) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(_documentVolumeHash),new org.web3j.abi.datatypes.DynamicBytes(_documentBlockLink),new org.web3j.abi.datatypes.generated.Uint256(_expirationTimestamp));
            this._documentVolumeHash = _documentVolumeHash;
            this._documentBlockLink = _documentBlockLink;
            this._expirationTimestamp = _expirationTimestamp;
        }

        public IndexEntry(Bytes32 _documentVolumeHash, DynamicBytes _documentBlockLink, Uint256 _expirationTimestamp) {
            super(_documentVolumeHash,_documentBlockLink,_expirationTimestamp);
            this._documentVolumeHash = _documentVolumeHash.getValue();
            this._documentBlockLink = _documentBlockLink.getValue();
            this._expirationTimestamp = _expirationTimestamp.getValue();
        }
    }

    public static class RoleAdminChangedEventResponse extends BaseEventResponse {
        public byte[] role;

        public byte[] previousAdminRole;

        public byte[] newAdminRole;
    }

    public static class RoleGrantedEventResponse extends BaseEventResponse {
        public byte[] role;

        public String account;

        public String sender;
    }

    public static class RoleRevokedEventResponse extends BaseEventResponse {
        public byte[] role;

        public String account;

        public String sender;
    }

    public static class SignedEntryEventResponse extends BaseEventResponse {
        public String signatory;

        public byte[] indexHash;

        public IndexEntry indexEntry;
    }
}
