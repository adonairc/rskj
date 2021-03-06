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

package org.ethereum.jsontestsuite.builder;

import co.rsk.trie.TrieStoreImpl;
import org.ethereum.core.AccountState;
import org.ethereum.core.Repository;
import org.ethereum.datasource.HashMapDB;
import org.ethereum.db.ByteArrayWrapper;
import org.ethereum.db.ContractDetails;
import org.ethereum.db.ContractDetailsCacheImpl;
import co.rsk.db.RepositoryImpl;
import org.ethereum.jsontestsuite.model.AccountTck;

import java.util.HashMap;
import java.util.Map;

import static org.ethereum.json.Utils.parseData;
import static org.ethereum.util.ByteUtil.wrap;

public class RepositoryBuilder {

    public static Repository build(Map<String, AccountTck> accounts){
        HashMap<ByteArrayWrapper, AccountState> stateBatch = new HashMap<>();
        HashMap<ByteArrayWrapper, ContractDetails> detailsBatch = new HashMap<>();

        for (String address : accounts.keySet()) {

            AccountTck accountTCK = accounts.get(address);
            AccountBuilder.StateWrap stateWrap = AccountBuilder.build(accountTCK);

            AccountState state = stateWrap.getAccountState();
            ContractDetails details = stateWrap.getContractDetails();

            stateBatch.put(wrap(parseData(address)), state);

            ContractDetailsCacheImpl detailsCache = new ContractDetailsCacheImpl(details);
            detailsCache.setDirty(true);

            detailsBatch.put(wrap(parseData(address)), detailsCache);
        }

        RepositoryImpl repositoryDummy = new RepositoryImpl(new TrieStoreImpl(new HashMapDB()));
        Repository track = repositoryDummy.startTracking();
        track.updateBatch(stateBatch, detailsBatch);
        track.commit();

        return repositoryDummy;
    }
}
