package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.CategoryConflictException;
import ru.practicum.exception.CategoryNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.storage.category.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new CategoryConflictException("Категория с таким именем уже существует");
        }
        Category categoryToSave = CategoryMapper.mapToCategory(newCategoryDto);
        categoryRepository.save(categoryToSave);

        return CategoryMapper.mapToCategoryDto(categoryToSave);
    }

    @Override
    public CategoryDto updateCategory(UpdateCategoryDto updateCategoryDto, Long catId) {
        Category categoryToUpdate = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException("Категории с таким id не существует"));

        if (categoryRepository.existsByName(updateCategoryDto.getName())
                && !updateCategoryDto.getName().equals(categoryToUpdate.getName())) {
            throw new CategoryConflictException("Категория с таким названием уже существует");
        }

        categoryToUpdate.setName(updateCategoryDto.getName());
        categoryRepository.save(categoryToUpdate);

        return CategoryMapper.mapToCategoryDto(categoryToUpdate);
    }

    @Override
    public void deleteCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new CategoryNotFoundException("Категории с таким id не существует");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> findAllCategories(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Параметры размеров выборки не корректны");
        }

        List<Category> categories = categoryRepository.findCategoriesWithOffsetAndLimit(from, size);

        return categories.stream()
                .map(CategoryMapper::mapToCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto findCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryNotFoundException("Категории с указанным id не существует"));

        return CategoryMapper.mapToCategoryDto(category);
    }
}
