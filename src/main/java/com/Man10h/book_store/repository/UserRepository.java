package com.Man10h.book_store.repository;

import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query(value = """
        select new com.Man10h.book_store.model.response.UserResponse(u.id, u.username, u.email, u.enabled, r.name)
        from UserEntity u 
        join RoleEntity r
        on u.roleEntity.id = r.id
""")
    public Page<UserResponse> getUsers(Pageable pageable);

    @Modifying
    @Query(value = """
        update UserEntity u set u.roleEntity.id = :roleId where u.id = :userId
""")
    public void updateUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Query(value = """
    select new com.Man10h.book_store.model.response.UserResponse(u.id, u.username, u.email, u.enabled, r.name)
        from UserEntity u 
        join RoleEntity r
        on u.roleEntity.id = r.id
        where u.username like %:username%
""")
    public Page<UserResponse> getUsersByUsername(@Param("username") String username, Pageable pageable);
}
