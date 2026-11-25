package com.pfc.thindesk.repository;

import com.pfc.thindesk.entity.TipoChamado;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TipoChamadoRepository extends MongoRepository<TipoChamado, String> {

    List<TipoChamado> findAllByOrderByNomeAsc();

    boolean existsByNome(String nome);
}
