package io.wrkshp.userprofile;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserProfileRepository extends CrudRepository<UserProfile, Long> {

    Optional<UserProfile> findById(Long id);

    Optional<UserProfile> findByEmail(String email);
}
