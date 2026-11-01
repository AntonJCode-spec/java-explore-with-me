package ru.practicum.controller.category;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.category.UpdateCategoryDto;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createNewCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return null;
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@Valid @RequestBody UpdateCategoryDto updateCategoryDto,
                                      @PathVariable Long catId) {
        return null;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        return;
    }
}
