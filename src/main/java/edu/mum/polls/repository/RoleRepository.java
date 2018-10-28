package edu.mum.polls.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.mum.polls.model.Role;
import edu.mum.polls.model.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
	
	public Optional<Role> findByName(RoleName name);
	
}
