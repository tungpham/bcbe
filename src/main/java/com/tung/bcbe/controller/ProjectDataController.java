package com.tung.bcbe.controller;

import com.tung.bcbe.model.Level;
import com.tung.bcbe.model.Project;
import com.tung.bcbe.model.Room;
import com.tung.bcbe.repository.LevelRepository;
import com.tung.bcbe.repository.ProjectRepository;
import com.tung.bcbe.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@Slf4j
public class ProjectDataController {
    
    @Autowired
    private LevelRepository levelRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @GetMapping("/projects/{prj_id}/levels")
    public List<Level> getData(@PathVariable(name = "prj_id") UUID projId) {
        List<Level> levels = levelRepository.findByProjectId(projId);
        for (Level level : levels) {
            List<Room> rooms = roomRepository.findByLevelId(level.getId());
            level.setRooms(rooms);   
        }
        return levels;
    }
    
    @PostMapping("/projects/{prj_id}/levels")
    public Level createLevel(@PathVariable(value = "prj_id") UUID prj_id, @RequestBody @Valid Level level) {
        return projectRepository.findById(prj_id).map(project -> {
                level.setProject(project);
                return levelRepository.save(level);
            }
        ).orElseThrow(Util.notFound(prj_id, Project.class));
    }
    
    @GetMapping("/levels/{lvl_id}")
    public Level getLevel(@PathVariable(value = "lvl_id") UUID lvl_id, 
                          @RequestParam(required = false, defaultValue = "false") String full) {
        Level level = levelRepository.findById(lvl_id).orElseThrow(Util.notFound(lvl_id, Level.class));
        if (full.equals("true")) {
            List<Room> rooms = roomRepository.findByLevelId(lvl_id);
            level.setRooms(rooms);
        }
        return level;
    }

    @DeleteMapping("/levels/{lvl_id}")
    public void deleteLevel(@PathVariable(value = "lvl_id") UUID lvl_id) {
        levelRepository.deleteById(lvl_id);
    }
    
    @PutMapping("/levels/{lvl_id}")
    public Level updateLevel(@PathVariable(value = "lvl_id") UUID lvl_id, @RequestBody @Valid Level update) {
        return levelRepository.findById(lvl_id).map(current -> {
            if (update.getDescription() != null && StringUtils.compare(current.getDescription(), update.getDescription()) != 0) {
                current.setDescription(update.getDescription());
            }
            if (update.getName() != null && StringUtils.compare(current.getName(), update.getName()) != 0) {
                current.setName(update.getName());
            }
            return levelRepository.save(current);
        }).orElseThrow(Util.notFound(lvl_id, Level.class));
    }
    
    @PostMapping("/levels/{lvl_id}/rooms")
    public Room createRoom(@PathVariable(value = "lvl_id") UUID lvl_id, @RequestBody @Valid Room room) {
        return levelRepository.findById(lvl_id).map(level -> {
            room.setLevel(level);
            return roomRepository.save(room);
        }).orElseThrow(Util.notFound(lvl_id, Level.class));
    }
    
    @GetMapping("/rooms/{room_id}")
    public Room getRoom(@PathVariable(value = "room_id") UUID roomId) {
        return roomRepository.findById(roomId).orElseThrow(Util.notFound(roomId, Room.class));
    }
    
    @PutMapping("/rooms/{room_id}")
    public Room updateRoom(@PathVariable(value = "room_id") UUID roomId, @RequestBody @Valid Room room) {
        return roomRepository.findById(roomId).map(current -> {
            if (room.getDescription() != null && !current.getDescription().equals(room.getDescription())) {
                current.setDescription(room.getDescription());
            }
            if (room.getName() != null && !current.getName().equals(room.getName())) {
                current.setName(room.getName());
            }
            return roomRepository.save(current);
        }).orElseThrow(Util.notFound(roomId, Room.class));
    }
    
    @DeleteMapping("/rooms/{room_id}")
    public void deleteRoom(@PathVariable(value = "room_id") UUID roomId) {
        roomRepository.deleteById(roomId);
    }
}
