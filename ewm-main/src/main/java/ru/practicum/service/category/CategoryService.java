package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(UpdateCategoryDto updateCategoryDto, Long catId);

    void deleteCategory(Long catId);

    List<CategoryDto> findAllCategories(int from, int size);

    CategoryDto findCategoryById(Long catId);
}
