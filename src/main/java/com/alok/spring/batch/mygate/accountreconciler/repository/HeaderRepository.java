package com.alok.spring.batch.mygate.accountreconciler.repository;

import com.alok.spring.batch.mygate.accountreconciler.model.Header;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeaderRepository extends JpaRepository<Header, Integer> {
}
