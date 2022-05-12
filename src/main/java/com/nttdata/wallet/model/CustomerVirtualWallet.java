package com.nttdata.wallet.model;

import java.io.Serializable;

import com.nttdata.wallet.entity.TypeDocument;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CustomerVirtualWallet implements Serializable {
	
	private Long idWallet;
	private Long idCustomer;
	private TypeDocument typeDocument;
	private String documentNumber;
	private String email_address;
	private String imeiPhone;
	private String phone_number;
	private String firstname;
	private String lastname;	

}
