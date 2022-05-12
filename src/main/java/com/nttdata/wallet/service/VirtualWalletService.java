package com.nttdata.wallet.service;

import com.nttdata.wallet.model.CardResp;
import com.nttdata.wallet.model.Card;
import com.nttdata.wallet.model.CardWallet;
import com.nttdata.wallet.model.MovementWalletResp;
import com.nttdata.wallet.model.VirtualWallet;
import com.nttdata.wallet.model.WalletResp;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VirtualWalletService {

		Flux<VirtualWallet> findAll();
		
		Mono<VirtualWallet> findById(Long idWallet);
	
		Mono<VirtualWallet> save(VirtualWallet wallet);
		
		Mono<VirtualWallet> update(VirtualWallet wallet);
		
		Mono<Void> delete (Long idWallet);
		
		Mono<WalletResp> registerWallet(VirtualWallet wallet);
		
		Mono<CardResp> associateYourWallet(CardWallet cardWallet);
		
		Mono<MovementWalletResp> walletTransaction(MovementWalletResp movementWalletResp);
}
