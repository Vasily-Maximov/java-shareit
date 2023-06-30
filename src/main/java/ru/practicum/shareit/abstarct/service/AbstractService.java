package ru.practicum.shareit.abstarct.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.abstarct.model.AbstractModel;
import ru.practicum.shareit.abstarct.repository.AbstractRepository;
import ru.practicum.shareit.exeption.ObjectValidationException;

import java.util.List;

@Slf4j
public class AbstractService<T extends AbstractModel> {

    protected final AbstractRepository<T> repository;

    public AbstractService(AbstractRepository<T> repository) {
        this.repository = repository;
    }

    public void add(T variable) {
        repository.add(variable);
    }

    public List<T> getAll() {
        return repository.getAll();
    }

    public void update(T variable) {
        repository.update(variable);
    }

    public T getById(Integer id) {
        return repository.getById(id);
    }

    public void delete(Integer id) {
        repository.delete(id);
    }

    public void checkField(String inputField, String nameField) {
        if (inputField != null) {
            if (inputField.trim().isEmpty()) {
                String message = String.format("Ошибка поле: %s, пустое или состоит из пробелов", nameField);
                log.error(message);
                throw new ObjectValidationException(message);
            }
        }
    }
}