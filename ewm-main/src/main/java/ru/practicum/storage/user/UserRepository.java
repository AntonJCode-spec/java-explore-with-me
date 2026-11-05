package ru.practicum.storage.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Query(nativeQuery = true,
    value = """
            SELECT *
            FROM users AS u
            ORDER BY id
            LIMIT :size
            OFFSET :from
            """)
    List<User> findUsersWithOffsetAndSize(@Param("from") int from, @Param("size") int size);

    @Query(nativeQuery = true,
    value = """
            SELECT *
            FROM users AS u
            WHERE id IN :ids
            ORDER BY id
            LIMIT :size
            OFFSET :from
            """)
    List<User> findUsersWithOffsetAndSizeWhereIdIn(@Param("ids") List<Long> ids,
                                                   @Param("from") int from,
                                                   @Param("size") int size);
}
