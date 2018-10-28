package edu.mum.polls.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.mum.polls.exception.ResourceNotFoundException;
import edu.mum.polls.model.User;
import edu.mum.polls.payloads.PagedResponse;
import edu.mum.polls.payloads.PollResponse;
import edu.mum.polls.payloads.UserIdAvailability;
import edu.mum.polls.payloads.UserProfile;
import edu.mum.polls.payloads.UserSummary;
import edu.mum.polls.repository.PollRepository;
import edu.mum.polls.repository.UserRepository;
import edu.mum.polls.repository.VoteRepository;
import edu.mum.polls.security.CurrentUser;
import edu.mum.polls.security.UserPrincipal;
import edu.mum.polls.service.PollService;
import edu.mum.polls.util.AppConstants;

@RestController
@RequestMapping("/api")
public class UserController {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PollRepository pollRepo;
	
	@Autowired
	private VoteRepository voteRepo;
	
	@Autowired
	private PollService pollService;
	
	@GetMapping("/user/me")
	@PreAuthorize("hasRole('USER')")
	public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
		return new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
	}
	
	@GetMapping("/user/checkUserNameAvailability")
	public UserIdAvailability checkUserIdAvailability(@RequestParam("username") String username) {
		Boolean available = !userRepo.existsByUsername(username);
		return new UserIdAvailability(available);
	}
	
	@GetMapping("/user/checkEmailAvailability")
	public UserIdAvailability checkEmailAvailability(@RequestParam ("email") String email) {
		Boolean available = !userRepo.existsByEmail(email);
		return new UserIdAvailability(available);
	}
	
	@GetMapping("users/{username}")
	public UserProfile getUserProfile(@RequestParam("username") String username) {
		User user = userRepo.findByEmail(username)
				.orElseThrow(()-> new ResourceNotFoundException("User", "username", username));
		
		long pollCount = pollRepo.countByCreatedBy(user.getId());
		long voteCount = voteRepo.countByUserId(user.getId());
		
		return new UserProfile(user.getId(), 
				user.getUsername(), 
				user.getName(), 
				user.getCreatedAt(), pollCount, voteCount);		
	}
	
	@GetMapping("/users/{username}/polls")
	public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable("username")String username,
			@CurrentUser UserPrincipal currentUser,
			@RequestParam(value="page",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value="size", defaultValue=AppConstants.DEFAULT_PAGE_SIZE) int size
			){
		return pollService.getPollsCreatedBy(username, currentUser, page, size);
	}
	
	@GetMapping("/users/{username}/votes")
	public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable("username")String username,
			@CurrentUser UserPrincipal currentUser,
			@RequestParam(value="page",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value="size", defaultValue=AppConstants.DEFAULT_PAGE_SIZE) int size
			){
		return pollService.getPollsVotedBy(username, currentUser, page, size);
	}
}
