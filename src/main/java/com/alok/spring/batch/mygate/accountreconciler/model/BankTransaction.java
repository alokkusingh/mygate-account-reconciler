package com.alok.spring.batch.mygate.accountreconciler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class BankTransaction {
    @Id
    private Long id;
    private Date date;
    private String docNo;
    private String description;
    private String chequeNo;
    private Double debit;
    private Double credit;
    private Date bankDate;
    private String txnId;
    private String utrNo;

    @Transient
    @Builder.Default
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public String getDescriptionWithQuot() {
        return "\"" + description + "\"";
    }

    public String getDateAsString() {
       return simpleDateFormat.format(date);
    }

    public String getBankDateAsString() {
        return simpleDateFormat.format(bankDate);
    }

    @Override
    public String toString() {
        return "BankTransaction{" +
                "id=" + id +
                ", date=" + simpleDateFormat.format(date) +
                ", docNo='" + docNo + '\'' +
                ", description='" + description + '\'' +
                ", chequeNo='" + chequeNo + '\'' +
                ", debit=" + debit +
                ", credit=" + credit +
                ", txnId=" + txnId +
                ", utrNo=" + utrNo +
                //", bankDate=" + bankDate==null?"":simpleDateFormat.format(bankDate) +
                '}';
    }
}
