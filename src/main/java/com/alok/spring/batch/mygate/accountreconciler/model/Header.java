package com.alok.spring.batch.mygate.accountreconciler.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Header {
    @Id
    @GeneratedValue
    private Integer id;
    private String line;
}
