package ru.practicum.shareit.abstarct.repository;

import ru.practicum.shareit.abstarct.model.AbstractModel;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public abstract class AbstractRepository<T extends AbstractModel> {

    private final Map<Integer, T> hashMapModel = new HashMap<>();

    private Integer globalId = 0;

    private Integer getNextId() {
        return ++globalId;
    }

    public void add(T variable) {
        variable.setId(getNextId());
        hashMapModel.put(globalId, variable);
    }

    public void update(T variable) {
        hashMapModel.put(variable.getId(), variable);
    }

    public T getById(Integer id) {
        return hashMapModel.get(id);
    }

    public List<T> getAll() {
        return new ArrayList<>(hashMapModel.values());
    }

    public void delete(Integer id) {
        hashMapModel.remove(id);
    }
}