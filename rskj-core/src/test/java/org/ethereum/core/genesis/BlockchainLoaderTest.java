/*
 * This file is part of RskJ
 * Copyright (C) 2017 RSK Labs Ltd.
 * (derived from ethereumJ library, Copyright (c) 2016 <ether.camp>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.ethereum.core.genesis;

import co.rsk.core.bc.BlockChainImplTest;
import co.rsk.trie.TrieStoreImpl;
import org.spongycastle.util.encoders.Hex;
import org.ethereum.config.BlockchainNetConfig;
import org.ethereum.config.Constants;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.Blockchain;
import org.ethereum.core.Repository;
import org.ethereum.db.BlockStore;
import org.ethereum.listener.EthereumListener;
import org.ethereum.vm.DataWord;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigInteger;

public class BlockchainLoaderTest {

    @Test
    public void testLoadBlockchainEmptyBlockchain() throws IOException {
        String jsonFile = "blockchain_loader_genesis.json";

        Blockchain blockchain = BlockChainImplTest.createBlockChain();

        SystemProperties systemProperties = Mockito.mock(SystemProperties.class);

        Constants constants = Mockito.mock(Constants.class);
        Mockito.when(constants.getInitialNonce()).thenReturn(BigInteger.ZERO);

        BlockchainNetConfig blockchainNetConfig = Mockito.mock(BlockchainNetConfig.class);
        Mockito.when(blockchainNetConfig.getCommonConstants()).thenReturn(constants);

        Mockito.when(systemProperties.getBlockchainConfig()).thenReturn(blockchainNetConfig);
        Mockito.when(systemProperties.genesisInfo()).thenReturn(jsonFile);

        BlockStore blockStore = Mockito.mock(BlockStore.class);
        Mockito.when(blockStore.getBestBlock()).thenReturn(null);

        EthereumListener ethereumListener = Mockito.mock(EthereumListener.class);

        Repository repository = blockchain.getRepository();

        BlockChainLoader blockChainLoader = new BlockChainLoader(blockchain, systemProperties, blockStore, repository, ethereumListener);

        blockChainLoader.loadBlockchain();

        Assert.assertEquals(5, repository.getAccountsKeys().size());

        Assert.assertEquals(BigInteger.valueOf(2000), repository.getBalance(Hex.decode("dabadabadabadabadabadabadabadabadaba0001")));
        Assert.assertEquals(BigInteger.valueOf(24), repository.getNonce(Hex.decode("dabadabadabadabadabadabadabadabadaba0001")));

        Assert.assertEquals(BigInteger.valueOf(1000), repository.getBalance(Hex.decode("dabadabadabadabadabadabadabadabadaba0002")));
        Assert.assertEquals(BigInteger.ZERO, repository.getNonce(Hex.decode("dabadabadabadabadabadabadabadabadaba0002")));

        Assert.assertEquals(BigInteger.valueOf(10), repository.getBalance(Hex.decode("77045e71a7a2c50903d88e564cd72fab11e82051")));
        Assert.assertEquals(BigInteger.valueOf(25), repository.getNonce(Hex.decode("77045e71a7a2c50903d88e564cd72fab11e82051")));
        Assert.assertEquals(DataWord.ONE, repository.getContractDetails(Hex.decode("77045e71a7a2c50903d88e564cd72fab11e82051")).get(DataWord.ZERO));
        Assert.assertEquals(new DataWord(3), repository.getContractDetails(Hex.decode("77045e71a7a2c50903d88e564cd72fab11e82051")).get(DataWord.ONE));
        Assert.assertEquals(274, repository.getContractDetails(Hex.decode("77045e71a7a2c50903d88e564cd72fab11e82051")).getCode().length);

    }

}
