package com.nttdata.wallet.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nttdata.wallet.entity.CardType;

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
public class Card {
	
	private Long idCard;
	private String carNumber;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private Date expirationDate;
	private CardType cardType;
	private String cvv;
	
	private String password;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss") 
	private Date creationDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private Date dateModified;
	
}
