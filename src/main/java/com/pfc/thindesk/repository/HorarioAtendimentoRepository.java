package com.pfc.thindesk.repository;

import com.pfc.thindesk.entity.HorarioAtendimento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HorarioAtendimentoRepository extends MongoRepository<HorarioAtendimento, String> {

    List<HorarioAtendimento> findAllByOrderBySetorAsc();
}
