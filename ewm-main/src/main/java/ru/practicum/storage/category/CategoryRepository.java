package ru.practicum.storage.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    @Query(value = "SELECT * FROM categories ORDER BY id LIMIT :size OFFSET :from", nativeQuery = true)
    List<Category> findCategoriesWithOffsetAndLimit(@Param("from") int from, @Param("size") int size);
}
