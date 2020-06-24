package com.alok.spring.batch.mygate.accountreconciler.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BankAccountTransaction {
    @Id
    private String utrNo;
    private Date bankDate;
    private String narration;
    private String valueDate;
    private String withdrawalAmount;
    private String depositAmount;
    private String closingBalance;

    @Transient
    @Builder.Default
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public String getBankDateAsString() {
        return simpleDateFormat.format(bankDate);
    }

}
