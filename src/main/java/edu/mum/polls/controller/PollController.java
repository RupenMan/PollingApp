package edu.mum.polls.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.mum.polls.model.Poll;
import edu.mum.polls.payloads.ApiResponse;
import edu.mum.polls.payloads.PagedResponse;
import edu.mum.polls.payloads.PollRequest;
import edu.mum.polls.payloads.PollResponse;
import edu.mum.polls.payloads.VoteRequest;
import edu.mum.polls.security.CurrentUser;
import edu.mum.polls.security.UserPrincipal;
import edu.mum.polls.service.PollService;
import edu.mum.polls.util.AppConstants;

@RestController
@RequestMapping("/api/polls")
public class PollController {
	
	@Autowired
	private PollService pollService;
	
	@GetMapping
	public PagedResponse<PollResponse> getPolls(
			@CurrentUser UserPrincipal currentUser,
			@RequestParam(value="page", defaultValue=AppConstants.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(value="size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size
			){
		return pollService.getAllPolls(currentUser, page, size);
	}
	
	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> createPoll(@Valid @RequestBody PollRequest pollRequest){
		Poll poll = pollService.createPoll(pollRequest);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{pollId}")
				.buildAndExpand(poll.getId())
				.toUri();
		
		return ResponseEntity.created(location)
				.body(new ApiResponse(true, "Poll Created Successfully"));
	}
	
	@GetMapping("/{pollId}")
	public PollResponse getPollById(@CurrentUser UserPrincipal currentUser, @PathVariable Long pollId) {
		return pollService.getPollById(currentUser, pollId);
	}
	
	@PostMapping("/{pollId}/votes")
	@PreAuthorize("hasRole('USER')")
	public PollResponse castVote(@CurrentUser UserPrincipal currentUser,
			@PathVariable Long pollId, @Valid @RequestBody VoteRequest voteRequest) {
		return pollService.castVoteAndGetUpdatedPoll(currentUser, pollId, voteRequest);
	}
	
	
	
	
}
