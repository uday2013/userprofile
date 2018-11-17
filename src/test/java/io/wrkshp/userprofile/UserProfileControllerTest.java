package io.wrkshp.userprofile;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserProfileControllerTest {

    private UserProfileController controller;
    private UserProfileRepository repository;

    @Before
    public void setUp() {
        repository = mock(UserProfileRepository.class);
        controller = new UserProfileController(repository);
    }

    @Test
    public void testEmptyGetAll() {
        doReturn(Collections.EMPTY_LIST).when(repository).findAll();

        ResponseEntity<List<UserProfile>> found = controller.getAll();

        verify(repository).findAll();
        assertThat(found.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(found.getBody()).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void testGetAll() {
        List<UserProfile> users = Arrays.asList(
                new UserProfile(1L, "John", "Doe", "johndoe@example.org"),
                new UserProfile(2L, "Jane", "Doe", "janedoe@example.org"));
        doReturn(users).when(repository).findAll();

        ResponseEntity<List<UserProfile>> found = controller.getAll();

        verify(repository).findAll();
        assertThat(found.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(found.getBody()).isEqualTo(users);
    }

    @Test
    public void testCreateWithNullArgReturnsBadRequest() {
        UserProfile userProfile = new UserProfile(null, "Doe", "johndoe@example.org");

        ResponseEntity<UserProfile> response = controller.create( userProfile );

        verify(repository, never()).save(any(UserProfile.class));
        verify(repository, never()).findByEmail(any(String.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.hasBody()).isFalse();


        userProfile = new UserProfile( "John", null, "johndoe@example.org");

        response = controller.create(userProfile);

        verify(repository, never()).save(any(UserProfile.class));
        verify(repository, never()).findByEmail(any(String.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.hasBody()).isFalse();


        userProfile = new UserProfile( "John", "Doe", null);

        response = controller.create(userProfile);

        verify(repository, never()).save(any(UserProfile.class));
        verify(repository, never()).findByEmail(any(String.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.hasBody()).isFalse();
   }

    @Test
    public void testCreateDuplicateEmailReturnsBadRequest() {
        UserProfile input = new UserProfile("John", "Doe", "johndoe@example.org");
        UserProfile existing = new UserProfile(1L, "John", "Doe", "johndoe@example.org");
        doReturn(Optional.of(existing)).when(repository).findByEmail(input.getEmail());

        ResponseEntity<UserProfile> response = controller.create(input);

        verify(repository, never()).save(any(UserProfile.class));
        verify(repository).findByEmail(input.getEmail());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.hasBody()).isFalse();
    }

    @Test
    public void testCreateSuccess() {
        UserProfile input = new UserProfile("John", "Doe", "johndoe@example.org");
        UserProfile expected = new UserProfile(1L, "John", "Doe", "johndoe@example.org");
        doReturn(expected).when(repository).save(input);
        doReturn(Optional.empty()).when(repository).findByEmail(input.getEmail());

        ResponseEntity<UserProfile> response = controller.create( input );

        verify(repository).save(input);
        verify(repository).findByEmail(input.getEmail());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
    }


    @Test
    public void testGetOne() {
        Optional<UserProfile> user = Optional.of(
                new UserProfile(1L, "John", "Doe", "johndoe@example.org"));
        doReturn(user).when(repository).findById(1L);

        ResponseEntity<UserProfile> found = controller.getOne(1L);

        verify(repository).findById(1L);
        assertThat(found.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(found.getBody()).isEqualTo(user.get());
    }


    @Test
    public void testGetOneNotFound() {
        Optional<UserProfile> user = Optional.empty();
        doReturn(user).when(repository).findById(1L);

        ResponseEntity<UserProfile> found = controller.getOne(1L);

        verify(repository).findById(1L);
        assertThat(found.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(found.hasBody()).isFalse();
    }

    @Test
    public void testDeleteSuccess() {
        Optional<UserProfile> user = Optional.of(new UserProfile(1L, "John", "Doe", "johndoe@example.org"));
        doReturn(user).when(repository).findById(1L);

        ResponseEntity<UserProfile> found = controller.delete(1L);

        verify(repository).delete(user.get());
        verify(repository).findById(1L);
        assertThat(found.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(found.hasBody()).isFalse();
    }

    @Test
    public void testDeleteReturnsNotFound() {
        Optional<UserProfile> user = Optional.empty();
        doReturn(user).when(repository).findById(1L);

        ResponseEntity<UserProfile> found = controller.delete(1L);

        verify(repository,never()).delete(any(UserProfile.class));
        verify(repository).findById(1L);
        assertThat(found.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(found.hasBody()).isFalse();
    }

}
