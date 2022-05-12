package com.nttdata.wallet.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.nttdata.wallet.model.CustomerVirtualWallet;
import com.nttdata.wallet.model.VirtualWallet;
import com.nttdata.wallet.service.VirtualWalletService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CustomerConsumer {
	
	@Value("${api.kafka-uri.customer-topic-respose}")
	String customerTopicSave;
	@Autowired
	VirtualWalletService virtualWalletService;
	
	@KafkaListener(topics = "${api.kafka-uri.customer-topic-respose}", groupId = "group_id")
	public void customerConsumer(CustomerVirtualWallet customerWallet) {
		log.info("customerConsumer["+customerTopicSave+"]:" + customerWallet.toString());		
		VirtualWallet wallet=this.virtualWalletService.findById(customerWallet.getIdWallet()).blockOptional().get();
		wallet.setIdCustomer(customerWallet.getIdCustomer());
		this.virtualWalletService.update(wallet).subscribe();
		log.info("customerConsumer[Save]:" + wallet);
	}
	
}
