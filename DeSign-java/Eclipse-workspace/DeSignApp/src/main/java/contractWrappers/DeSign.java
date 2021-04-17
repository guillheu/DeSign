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
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.StaticStruct;
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
import org.web3j.tuples.generated.Tuple2;
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
    public static final String BINARY = "608060405234801561001057600080fd5b5061001c60003361004b565b6100467f9838a05512653d899e198165cc2a8305c24ac29892037f9cab63bdb1531218453361004b565b6100f7565b6100558282610059565b5050565b6000828152602081815260408083206001600160a01b038516845290915290205460ff16610055576000828152602081815260408083206001600160a01b03851684529091529020805460ff191660011790556100b33390565b6001600160a01b0316816001600160a01b0316837f2f8788117e7eff1d82e926ec794901d17c78024a50270940304540a733656f0d60405160405180910390a45050565b6106dd806101066000396000f3fe608060405234801561001057600080fd5b506004361061009e5760003560e01c806391d148541161006657806391d148541461014c578063a217fddf1461015f578063adad536714610167578063c993f49c1461018e578063d547741f146101a15761009e565b806301ffc9a7146100a3578063248a9ca3146100cb5780632f2ff15d146100fc57806336568abe146101115780637774d52314610124575b600080fd5b6100b66100b1366004610633565b6101b4565b60405190151581526020015b60405180910390f35b6100ee6100d93660046105b6565b60009081526020819052604090206001015490565b6040519081526020016100c2565b61010f61010a3660046105ce565b6101eb565b005b61010f61011f3660046105ce565b61027f565b6101376101323660046105b6565b6102f9565b604080519283526020830191909152016100c2565b6100b661015a3660046105ce565b610339565b6100ee600081565b6100ee7f9838a05512653d899e198165cc2a8305c24ac29892037f9cab63bdb15312184581565b61010f61019c366004610608565b610362565b61010f6101af3660046105ce565b61044d565b60006001600160e01b03198216637965db0b60e01b14806101e557506301ffc9a760e01b6001600160e01b03198316145b92915050565b600082815260208190526040902060010154610208905b3361015a565b6102715760405162461bcd60e51b815260206004820152602f60248201527f416363657373436f6e74726f6c3a2073656e646572206d75737420626520616e60448201526e0818591b5a5b881d1bc819dc985b9d608a1b60648201526084015b60405180910390fd5b61027b82826104cd565b5050565b6001600160a01b03811633146102ef5760405162461bcd60e51b815260206004820152602f60248201527f416363657373436f6e74726f6c3a2063616e206f6e6c792072656e6f756e636560448201526e103937b632b9903337b91039b2b63360891b6064820152608401610268565b61027b8282610551565b6000818152600160208181526040808420815180830190925280548083529301549181018290528392909161032f90429061067a565b9250925050915091565b6000918252602082815260408084206001600160a01b0393909316845291905290205460ff1690565b61038c7f9838a05512653d899e198165cc2a8305c24ac29892037f9cab63bdb15312184533610339565b6103cc5760405162461bcd60e51b81526020600482015260116024820152704d555354204245205349474e41544f525960781b6044820152606401610268565b604051806040016040528083815260200182426103e99190610662565b9052600084815260016020818152604092839020845180825594820151920182905582519384528301520160405190819003812090849033907fb90cc5a8e6721ffad5ddfb72031796e1a86a9221db4d12d87b90f64e6af8cfdc90600090a4505050565b60008281526020819052604090206001015461046890610202565b6102ef5760405162461bcd60e51b815260206004820152603060248201527f416363657373436f6e74726f6c3a2073656e646572206d75737420626520616e60448201526f2061646d696e20746f207265766f6b6560801b6064820152608401610268565b6104d78282610339565b61027b576000828152602081815260408083206001600160a01b03851684529091529020805460ff1916600117905561050d3390565b6001600160a01b0316816001600160a01b0316837f2f8788117e7eff1d82e926ec794901d17c78024a50270940304540a733656f0d60405160405180910390a45050565b61055b8282610339565b1561027b576000828152602081815260408083206001600160a01b0385168085529252808320805460ff1916905551339285917ff6391f5c32d9c69d2a47ea670b442974b53935d1edc7fd64eb21e047a839171b9190a45050565b6000602082840312156105c7578081fd5b5035919050565b600080604083850312156105e0578081fd5b8235915060208301356001600160a01b03811681146105fd578182fd5b809150509250929050565b60008060006060848603121561061c578081fd5b505081359360208301359350604090920135919050565b600060208284031215610644578081fd5b81356001600160e01b03198116811461065b578182fd5b9392505050565b6000821982111561067557610675610691565b500190565b60008282101561068c5761068c610691565b500390565b634e487b7160e01b600052601160045260246000fdfea2646970667358221220de00e1080c6d58d260d5275d95e6aaa001fff7f85b2fd376f64ce39194c3597e64736f6c63430008030033";

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

    public RemoteFunctionCall<Tuple2<byte[], BigInteger>> getIndexData(byte[] _indexHash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETINDEXDATA, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_indexHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<byte[], BigInteger>>(function,
                new Callable<Tuple2<byte[], BigInteger>>() {
                    @Override
                    public Tuple2<byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<byte[], BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
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

    public RemoteFunctionCall<TransactionReceipt> signMerkleRoot(byte[] _indexHash, byte[] documentVolumeHash, BigInteger _secondsBeforeExpiration) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SIGNMERKLEROOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(_indexHash), 
                new org.web3j.abi.datatypes.generated.Bytes32(documentVolumeHash), 
                new org.web3j.abi.datatypes.generated.Uint256(_secondsBeforeExpiration)), 
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

    public static class IndexEntry extends StaticStruct {
        public byte[] _documentVolumeHash;

        public BigInteger _expirationTimestamp;

        public IndexEntry(byte[] _documentVolumeHash, BigInteger _expirationTimestamp) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(_documentVolumeHash),new org.web3j.abi.datatypes.generated.Uint256(_expirationTimestamp));
            this._documentVolumeHash = _documentVolumeHash;
            this._expirationTimestamp = _expirationTimestamp;
        }

        public IndexEntry(Bytes32 _documentVolumeHash, Uint256 _expirationTimestamp) {
            super(_documentVolumeHash,_expirationTimestamp);
            this._documentVolumeHash = _documentVolumeHash.getValue();
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
