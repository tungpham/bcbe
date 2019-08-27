package com.tung.bcbe.controller;

import com.tung.bcbe.model.Level;
import com.tung.bcbe.model.Node;
import com.tung.bcbe.model.Project;
import com.tung.bcbe.model.Room;
import com.tung.bcbe.model.Selection;
import com.tung.bcbe.repository.LevelRepository;
import com.tung.bcbe.repository.NodeRepository;
import com.tung.bcbe.repository.ProjectRepository;
import com.tung.bcbe.repository.RoomRepository;
import com.tung.bcbe.repository.SelectionRepository;
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

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Autowired
    private SelectionRepository selectionRepository;

    @Autowired
    private NodeRepository nodeRepository;

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
            if (update.getNumber() != null && update.getNumber() != current.getNumber()) {
                current.setNumber(update.getNumber());
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
        Room room = roomRepository.findById(roomId).orElseThrow(Util.notFound(roomId, Room.class));

        /*
        Clumsy way of filter out the children and grandchildren of the category so that the data returned isn't huge
        because Node children is always eagerly fetched.
         */
        room.setSelectionList(room.getSelectionList().stream().distinct().map(s -> {
           s.getCategory().setChildren(null);
           return s;
        }).collect(Collectors.toList()));
        return room;
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

    @PostMapping("/rooms/{room_id}/categories/{category_id}/selections/{selection_id}")
    public Selection saveSelection(@PathVariable(value = "room_id") UUID roomId,
                                   @PathVariable(value = "category_id") UUID categoryId,
                                   @PathVariable(value = "selection_id") UUID selectionId,
                                   @RequestBody @Valid Selection selection) {
        return roomRepository.findById(roomId).map(room ->
            nodeRepository.findById(categoryId).map(category ->
                nodeRepository.findById(selectionId).map(finalSelection -> {
                    selection.setCategory(category);
                    selection.setSelection(finalSelection);
                    selection.setRoom(room);
                    return selectionRepository.save(selection);
                }).orElseThrow(Util.notFound(selectionId, Node.class))
            ).orElseThrow(Util.notFound(categoryId, Node.class))
        ).orElseThrow(Util.notFound(roomId, Room.class));
    }

    @Transactional
    @DeleteMapping("/selections/{id}")
    public void deleteSelection(@PathVariable(value = "id") UUID id) {
        selectionRepository.deleteById(id);
    }

    @GetMapping("/selections/{id}")
    public Selection getSelection(@PathVariable(value = "id") UUID id) {
        return selectionRepository.findById(id).orElseThrow(Util.notFound(id, Selection.class));
    }

    @PutMapping("/selections/{id}")
    public Selection updateSelection(@PathVariable(value = "id") UUID id, @RequestBody @Valid Selection update) {
        return selectionRepository.findById(id).map(s -> {
            if (update.getOption() != null) {
                s.setOption(update.getOption());
            }
            if (update.getBreadcrumb() != null) {
                s.setBreadcrumb(update.getBreadcrumb());
            }
            return selectionRepository.save(s);
        }).orElseThrow(Util.notFound(id, Selection.class));
    }
}
