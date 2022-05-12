package com.nttdata.wallet.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.nttdata.wallet.model.VirtualWallet;

public interface VirtualWalletRepository extends ReactiveMongoRepository<VirtualWallet, Long>{
}
