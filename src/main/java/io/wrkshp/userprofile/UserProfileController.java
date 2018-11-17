package io.wrkshp.userprofile;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user-profiles")
public class UserProfileController {

    private UserProfileRepository repository;

    public UserProfileController(UserProfileRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<UserProfile> create(@RequestBody UserProfile userProfile) {

        if (userProfile.getFirstName() == null ||
                userProfile.getLastName() == null ||
                userProfile.getEmail() == null ||
                repository.findByEmail(userProfile.getEmail()).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        UserProfile userProfileSaved = repository.save(userProfile);
        return new ResponseEntity<>(userProfileSaved, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserProfile> getOne(@PathVariable Long id) {
        Optional<UserProfile> userProfile = repository.findById(id);
        return (userProfile.isPresent()) ?
                ResponseEntity.ok(userProfile.get()) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<UserProfile>> getAll() {
        List<UserProfile> users = new ArrayList<>((int)repository.count());
        repository.findAll().forEach(users::add);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("{id}")
    @Transactional
    public ResponseEntity<UserProfile> delete(@PathVariable Long id) {
        Optional<UserProfile> doomed = repository.findById(id);
        doomed.ifPresent(repository::delete);
        return new ResponseEntity<>((doomed.isPresent()) ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
    }
}
