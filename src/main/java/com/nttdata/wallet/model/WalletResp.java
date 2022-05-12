package com.nttdata.wallet.model;

import java.util.Map;

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
public class WalletResp {

	private VirtualWallet virtualWallet;
	private Map<String, Object> mensaje;
	
}
