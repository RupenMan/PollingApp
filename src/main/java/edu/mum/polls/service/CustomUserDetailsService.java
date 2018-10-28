package edu.mum.polls.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.mum.polls.model.User;
import edu.mum.polls.repository.UserRepository;
import edu.mum.polls.security.UserPrincipal;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		User user = userRepo.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
		return UserPrincipal.create(user);
	}

	public UserDetails loadUserById(Long id) throws UsernameNotFoundException{
		User user = userRepo.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with Id: " + id));
		return UserPrincipal.create(user);
	}
}
