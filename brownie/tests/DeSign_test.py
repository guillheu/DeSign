from brownie import accounts, DeSign
from brownie.convert import to_bytes
from brownie.exceptions import *
import brownie
import pytest

@pytest.fixture(autouse=True)
def setupVariables():
	global owner
	owner = accounts[0]

@pytest.fixture
def DeSignContract():
	return DeSign.deploy({'from':owner})

def testCheckOwner(DeSignContract):
	assert DeSignContract.owner() == owner