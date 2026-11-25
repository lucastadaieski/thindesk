package com.pfc.thindesk.repository;

import com.pfc.thindesk.entity.Cliente;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClienteRepository extends MongoRepository<Cliente, String> {

    List<Cliente> findAllByOrderByNomeAsc();
}
