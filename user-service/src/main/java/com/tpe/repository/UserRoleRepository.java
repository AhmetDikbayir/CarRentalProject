package com.tpe.repository;


import com.tpe.domain.Role;
import com.tpe.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface UserRoleRepository extends JpaRepository<Role,Long> {

    @Query("SELECT r FROM Role r WHERE r.roleType = ?1")
    Optional<Role> findByEnumRoleEquals(RoleType roleType);


}
