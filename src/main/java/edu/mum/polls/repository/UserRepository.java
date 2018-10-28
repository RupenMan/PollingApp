package edu.mum.polls.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.mum.polls.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	public Optional<User> findByEmail(String email);
	
	public Optional<User> findByUsernameOrEmail(String email, String username);
	
	public Optional<User> findByUsername(String username);
	
	public Boolean existsByUsername(String username);
	
	public Boolean existsByEmail(String email);
	
	public Optional<User> findById(Long id);
	
	public List<User> findByIdIn(List<Long> creatorId);

}
