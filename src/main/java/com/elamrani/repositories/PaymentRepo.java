package com.elamrani.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elamrani.entites.Payment;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long>{

}
