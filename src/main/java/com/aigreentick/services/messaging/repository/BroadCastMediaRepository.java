package com.aigreentick.services.messaging.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.aigreentick.services.messaging.model.BroadcastMedia;

@Repository
public interface BroadCastMediaRepository extends MongoRepository<BroadcastMedia,String> {
    
}

