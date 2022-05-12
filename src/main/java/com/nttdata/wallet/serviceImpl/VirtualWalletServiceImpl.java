package com.nttdata.wallet.serviceImpl;

import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.nttdata.wallet.entity.AssociatedWallet;
import com.nttdata.wallet.model.Card;
import com.nttdata.wallet.model.CardResp;
import com.nttdata.wallet.model.CardWallet;
import com.nttdata.wallet.model.VirtualWallet;
import com.nttdata.wallet.model.WalletResp;
import com.nttdata.wallet.model.CustomerVirtualWallet;
import com.nttdata.wallet.model.MovementWalletResp;
import com.nttdata.wallet.repository.VirtualWalletRepository;
import com.nttdata.wallet.service.VirtualWalletService;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class VirtualWalletServiceImpl implements VirtualWalletService {
	
	@Autowired
	VirtualWalletRepository virtualWalletRepository;
	@Autowired
	KafkaTemplate<String, CustomerVirtualWallet> kafkaTemplate;
	@Autowired
	KafkaTemplate<String, CardWallet> kafkaTemplateCard;
	@Autowired
	KafkaTemplate<String, MovementWalletResp> kafkaTemplateMovement;
	
	@Value("${api.kafka-uri.customer-topic}")
	String customerTopic;
	@Value("${api.kafka-uri.card-topic}")
	String cardTopic;
	@Value("${api.kafka-uri.movement-wallet-topic}")
	String movementTopic;
	
	
	@Override
	public Flux<VirtualWallet> findAll() {
		// TODO Auto-generated method stub
		return virtualWalletRepository.findAll();
	}
	
	
	@Override
	public Mono<VirtualWallet> findById(Long idWallet) {
		// TODO Auto-generated method stub
		return virtualWalletRepository.findById(idWallet);
	}
	
	
	@Override
	public Mono<VirtualWallet> save(VirtualWallet wallet) {
		
		Long count = this.findAll().collect(Collectors.counting()).blockOptional().get();
		Long idWallet;
		if (count != null) {
			if (count <= 0) {
				idWallet = Long.valueOf(0);
			} else {
				idWallet = this.findAll().collect(Collectors.maxBy(Comparator.comparing(VirtualWallet::getIdWallet)))
						.blockOptional().get().get().getIdWallet();
			}

		} else {
			idWallet = Long.valueOf(0);

		}
		wallet.setCreationDate(Calendar.getInstance().getTime());
		wallet.setIdWallet(idWallet + 1);
		return virtualWalletRepository.insert(wallet);
	}
	
	
	@Override
	public Mono<VirtualWallet> update(VirtualWallet wallet) {
		wallet.setDateModified(Calendar.getInstance().getTime());
		return virtualWalletRepository.save(wallet);
	}
	
	
	@Override
	public Mono<Void> delete(Long idWallet) {
		// TODO Auto-generated method stub
		return virtualWalletRepository.deleteById(idWallet);
	}
	
	
	@Override
	public Mono<WalletResp> registerWallet(VirtualWallet wallet) {

		WalletResp response = new WalletResp();
		response.setMensaje(new HashMap<String, Object>());
		VirtualWallet walletFind = new VirtualWallet();
		walletFind.setPhone_number(wallet.getPhone_number());
		walletFind = this.virtualWalletRepository.findOne(Example.of(walletFind)).blockOptional().orElse(null);
		wallet.setAssociatedWallet(AssociatedWallet.CardNotAssociated);
		if (walletFind == null) {
			return this.save(wallet).map(e -> {
				CustomerVirtualWallet customerWallet = new CustomerVirtualWallet();
				customerWallet.setTypeDocument(wallet.getTypeDocument());
				customerWallet.setDocumentNumber(wallet.getDocumentNumber());
				customerWallet.setEmail_address(wallet.getEmail_address());
				customerWallet.setImeiPhone(wallet.getImeiPhone());
				customerWallet.setPhone_number(wallet.getPhone_number());
				customerWallet.setIdWallet(e.getIdWallet());
				log.info("Send kafka:" + customerTopic + " -->" + customerWallet);
				this.kafkaTemplate.send(customerTopic, customerWallet);
				response.setVirtualWallet(e);
				response.getMensaje().put("status", "success");
				return response;
			});

		} else {
			response.setVirtualWallet(walletFind);
			response.getMensaje().put("status", "error");
			response.getMensaje().put("mensaje", "El numero de telefono ingresado ya se encuentra registrado");
			return Mono.just(response);
		}
		
	}
	
	
	@Override
	public Mono<CardResp> associateYourWallet(CardWallet cardWallet) {
		CardResp cardResponse = new CardResp();
		cardResponse.setVirtualWallet(cardWallet.getVirtualWallet());
		cardResponse.setCardNumber(cardWallet.getCard().getCarNumber());
		cardResponse.setMensaje(new HashMap<String, Object>());
		VirtualWallet wallet = this.findById(cardWallet.getVirtualWallet().getIdWallet()).blockOptional().orElse(null);
		if (wallet != null && cardWallet.getCard().getCarNumber() != null) {
			if (wallet.getAssociatedWallet() == AssociatedWallet.CardNotAssociated) {
				cardResponse.getMensaje().put("status", "success");
				cardResponse.getMensaje().put("mensaje", "Procesando asociacion de tarjeta");
				log.info("Send kafka:" + cardTopic + " -->" + cardWallet);
				kafkaTemplateCard.send(cardTopic, cardWallet);
			} else {
				cardResponse.getMensaje().put("status", "error");
				cardResponse.getMensaje().put("mensajeWallet", "La cartera ya fue asignada a una cuenta.");
			}
		} else {
			cardResponse.getMensaje().put("status", "error");
			if (wallet == null) {
				cardResponse.getMensaje().put("mensajeWallet", "El codigo del monedero no existe.");
			}
			if (cardWallet.getCard().getCarNumber() == null) {
				cardResponse.getMensaje().put("mensajeCardNumber", "Ingrese el nro de la tarjeta.");
			}

		}
		return Mono.just(cardResponse);
	}
	
	
	@Override
	public Mono<MovementWalletResp> walletTransaction(MovementWalletResp movementWalletResp) {
		Map<String, Object> map = new HashMap<String, Object>();
		VirtualWallet walletOriginFind = new VirtualWallet();
		walletOriginFind.setPhone_number(movementWalletResp.getOriginPhoneNumber());
		VirtualWallet walletDestinyFind = new VirtualWallet();
		walletDestinyFind.setPhone_number(movementWalletResp.getDestinyPhoneNumber());
		VirtualWallet walletOrigin = this.virtualWalletRepository.findOne(Example.of(walletOriginFind)).blockOptional().orElse(null);
		VirtualWallet walletDestiny = this.virtualWalletRepository.findOne(Example.of(walletDestinyFind)).blockOptional()
				.orElse(null);
		if ((walletOrigin != null && walletOrigin.getAssociatedWallet() == AssociatedWallet.AssociatedCard)
				&& (walletDestiny != null && walletDestiny.getAssociatedWallet() == AssociatedWallet.AssociatedCard)) {
			map.put("status", "procesando transaccion");
			movementWalletResp.setIdOriginWallet(walletOrigin.getIdWallet());
			movementWalletResp.setIdDestinyWallet(walletDestiny.getIdWallet());
			movementWalletResp.setIdCard(walletOrigin.getIdCard());
			movementWalletResp.setOriginIdBankAccount(walletOrigin.getIdBankAccount());
			movementWalletResp.setDestinyIdBankAccount(walletDestiny.getIdBankAccount());
			log.info("Send kafka:" + movementTopic + " -->" + movementWalletResp);
			this.kafkaTemplateMovement.send(movementTopic, movementWalletResp);
		} else {
			map.put("status", "error");
			if (walletOrigin == null) {
				map.put("WalletOrigen", "No existe la cartera origen");
			} else {
				if (walletOrigin.getAssociatedWallet() != AssociatedWallet.AssociatedCard) {
					map.put("WalletOrigen", "El monedero no tiene una cuenta asignada");
				}
			}
			if (walletDestiny == null) {
				map.put("WalletDestino", "No existe la cartera destino");
			} else {
				if (walletDestiny.getAssociatedWallet() != AssociatedWallet.AssociatedCard) {
					map.put("WalletDestino", "El monedero no tiene una cuenta asignada");
				}
			}

		}
		movementWalletResp.setMensaje(map);
		return Mono.just(movementWalletResp);
	}
	
}
