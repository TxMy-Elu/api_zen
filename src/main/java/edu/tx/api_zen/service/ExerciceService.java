package edu.tx.api_zen.service;

import edu.tx.api_zen.dao.ExerciceDao;
import edu.tx.api_zen.dto.ExerciceCreateDto;
import edu.tx.api_zen.dto.ExerciceDto;
import edu.tx.api_zen.mapper.ExerciceMapper;
import edu.tx.api_zen.model.Exercice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciceService {

    private final ExerciceDao exerciceDao;

    public List<ExerciceDto> listAll() {
        return exerciceDao.findAll().stream().map(ExerciceMapper::toDto).collect(Collectors.toList());
    }

    public Optional<ExerciceDto> getById(Integer id) {
        return exerciceDao.findById(id).map(ExerciceMapper::toDto);
    }

    public ExerciceDto create(ExerciceCreateDto dto) {
        Exercice e = ExerciceMapper.toEntity(dto);
        Exercice saved = exerciceDao.save(e);
        return ExerciceMapper.toDto(saved);
    }

    public ExerciceDto update(Integer id, ExerciceCreateDto dto) {
        Optional<Exercice> opt = exerciceDao.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("Exercice not found");
        Exercice e = opt.get();
        e.setNom(dto.getNom());
        e.setDureeInspiration(dto.getDureeInspiration());
        e.setDureeApnee(dto.getDureeApnee());
        e.setDureeExpiration(dto.getDureeExpiration());
        e.setDescription(dto.getDescription());
        Exercice saved = exerciceDao.save(e);
        return ExerciceMapper.toDto(saved);
    }

    public void delete(Integer id) {
        exerciceDao.deleteById(id);
    }
}
