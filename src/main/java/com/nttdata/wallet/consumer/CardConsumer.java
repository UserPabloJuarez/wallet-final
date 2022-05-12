package com.nttdata.wallet.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.nttdata.wallet.model.CardWallet;
import com.nttdata.wallet.model.VirtualWallet;
import com.nttdata.wallet.service.VirtualWalletService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CardConsumer {

	@Autowired
	VirtualWalletService virtualWalletService;
	@KafkaListener(topics = "${api.kafka-uri.card-topic-respose}", groupId = "group_id")
	public void cardConsumer(CardWallet cardWallet) {
		log.info("cardConsumer [CardWallet]:" + cardWallet.toString());
		VirtualWallet wallet=this.virtualWalletService.findById(cardWallet.getVirtualWallet().getIdWallet()).blockOptional().orElse(null);
		if(wallet!=null) {
			wallet.setAssociatedWalletMessage(cardWallet.getVirtualWallet().getAssociatedWalletMessage());
			wallet.setAssociatedWallet(cardWallet.getVirtualWallet().getAssociatedWallet());
			wallet.setCardType(cardWallet.getVirtualWallet().getCardType());
			wallet.setIdBankAccount(cardWallet.getVirtualWallet().getIdBankAccount());	
			this.virtualWalletService.update(wallet).subscribe();
		}else {
			log.info("Wallet no encontrado [CardWallet]:" + cardWallet.toString());
		}
		
	}
	
}
