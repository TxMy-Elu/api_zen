package edu.tx.api_zen.service;

import edu.tx.api_zen.dao.ExercerDao;
import edu.tx.api_zen.dao.ExerciceDao;
import edu.tx.api_zen.dao.UserDao;
import edu.tx.api_zen.dto.ExercerDto;
import edu.tx.api_zen.mapper.ExercerMapper;
import edu.tx.api_zen.model.Exercer;
import edu.tx.api_zen.model.Exercice;
import edu.tx.api_zen.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExercerService {

    private final ExercerDao exercerDao;
    private final UserDao userDao;
    private final ExerciceDao exerciceDao;

    public List<ExercerDto> listAllPublic() {
        return exercerDao.findAll().stream().map(ExercerMapper::toDto).collect(Collectors.toList());
    }

    public List<ExercerDto> listByUser(Integer userId) {
        return exercerDao.findByUserId(userId).stream().map(ExercerMapper::toDto).collect(Collectors.toList());
    }

    public List<ExercerDto> listByExercice(Integer exerciceId) {
        return exercerDao.findByExercice_IdExercice(exerciceId).stream().map(ExercerMapper::toDto).collect(Collectors.toList());
    }

    public Optional<ExercerDto> getById(Integer id) {
        return exercerDao.findById(id).map(ExercerMapper::toDto);
    }

    public ExercerDto create(Integer userId, Integer exerciceId, Instant completedAt) {
        User user = userDao.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Exercice exo = exerciceDao.findById(exerciceId).orElseThrow(() -> new IllegalArgumentException("Exercice not found"));
        Exercer toSave = new Exercer();
        toSave.setUser(user);
        toSave.setExercice(exo);
        toSave.setCompletedAt(completedAt == null ? Instant.now() : completedAt);
        Exercer saved = exercerDao.save(toSave);
        return ExercerMapper.toDto(saved);
    }

    public void delete(Integer id) {
        exercerDao.deleteById(id);
    }
}
