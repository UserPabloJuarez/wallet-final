package com.nttdata.wallet.controller;

import com.nttdata.wallet.service.VirtualWalletService;
import com.nttdata.wallet.model.CardResp;
import com.nttdata.wallet.model.CardWallet;
import com.nttdata.wallet.model.MovementWalletResp;
import com.nttdata.wallet.model.VirtualWallet;
import com.nttdata.wallet.model.WalletResp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping("/api/v1/virtual/wallet")
public class VirtualWalletController {

	@Autowired
	VirtualWalletService virtualWalletService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<VirtualWallet> findAllx() {
		return virtualWalletService.findAll();

	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ResponseEntity<VirtualWallet>> save(@RequestBody VirtualWallet wallet) {
		return virtualWalletService.save(wallet).map(_wallet -> ResponseEntity.ok().body(_wallet)).onErrorResume(e -> {
			log.info("Error:" + e.getMessage());
			return Mono.just(ResponseEntity.badRequest().build());
		});
	}

	@GetMapping("/{idWallet}")
	public Mono<ResponseEntity<VirtualWallet>> findByIdx(@PathVariable(name = "idWallet") Long idWallet) {
		return virtualWalletService.findById(idWallet).map(wallet -> ResponseEntity.ok().body(wallet)).onErrorResume(e -> {
			log.info("Error:" + e.getMessage());
			return Mono.just(ResponseEntity.badRequest().build());
		}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@PutMapping
	public Mono<ResponseEntity<VirtualWallet>> update(@RequestBody VirtualWallet wallet) {
		Mono<VirtualWallet> mono = virtualWalletService.findById(wallet.getIdWallet()).flatMap(objWallet -> {
			return virtualWalletService.update(wallet);
		});
		return mono.map(_wallet -> {
			return ResponseEntity.ok().body(_wallet);
		}).onErrorResume(e -> {
			log.info("Error:" + e.getMessage());
			return Mono.just(ResponseEntity.badRequest().build());
		}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@DeleteMapping("/{idWallet}")
	public Mono<ResponseEntity<Void>> deletex(@PathVariable(name = "idWallet") Long idWallet) {
		Mono<VirtualWallet> _wallet = virtualWalletService.findById(idWallet);
		_wallet.subscribe();
		VirtualWallet wallet=_wallet.toFuture().join();
		if (wallet != null) {
			return virtualWalletService.delete(idWallet).map(r -> ResponseEntity.ok().<Void>build());
		} else {
			return Mono.just(ResponseEntity.noContent().build());
		} 
				
	 
	}

	@PostMapping("/registerWallet")
	public Mono<ResponseEntity<WalletResp>> registerWallet(@RequestBody VirtualWallet wallet) {
		return virtualWalletService.registerWallet(wallet).map(_wallet -> ResponseEntity.ok().body(_wallet))
				.onErrorResume(e -> {
					log.info("Error:" + e.getMessage());
					return Mono.just(ResponseEntity.badRequest().build());
				});
	}

	@PostMapping("/associateYourWallet")
	public Mono<ResponseEntity<CardResp>> associateWallet(@RequestBody CardWallet cardWallet) {
		return virtualWalletService.associateYourWallet(cardWallet).map(_cardWallet -> ResponseEntity.ok().body(_cardWallet))
				.onErrorResume(e -> {
					log.info("Error:" + e.getMessage());
					return Mono.just(ResponseEntity.badRequest().build());
				});
	}

	@PostMapping("/walletTransaction")
	public Mono<ResponseEntity<MovementWalletResp>> walletTransaction(
			@RequestBody MovementWalletResp movementWalletResponse) {
		return virtualWalletService.walletTransaction(movementWalletResponse)
				.map(_walletTransaction -> ResponseEntity.ok().body(_walletTransaction)).onErrorResume(e -> {
					log.info("Error:" + e.getMessage());
					return Mono.just(ResponseEntity.badRequest().build());
				});
	}
	
}
