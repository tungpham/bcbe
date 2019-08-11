package com.tung.bcbe.repository;

import com.tung.bcbe.model.Room;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface RoomRepository extends PagingAndSortingRepository<Room, UUID> {
    List<Room> findByLevelId(UUID levelId);
}
