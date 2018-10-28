package edu.mum.polls.service;


import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import edu.mum.polls.exception.BadRequestException;
import edu.mum.polls.exception.ResourceNotFoundException;
import edu.mum.polls.model.Choice;
import edu.mum.polls.model.ChoiceVoteCount;
import edu.mum.polls.model.Poll;
import edu.mum.polls.model.User;
import edu.mum.polls.model.Vote;
import edu.mum.polls.payloads.PagedResponse;
import edu.mum.polls.payloads.PollRequest;
import edu.mum.polls.payloads.PollResponse;
import edu.mum.polls.payloads.VoteRequest;
import edu.mum.polls.repository.PollRepository;
import edu.mum.polls.repository.UserRepository;
import edu.mum.polls.repository.VoteRepository;
import edu.mum.polls.security.UserPrincipal;
import edu.mum.polls.util.AppConstants;
import edu.mum.polls.util.ModelMapper;

@Service
@Transactional
public class PollService {
	
	private static final Logger logger= LoggerFactory.getLogger(PollService.class);
	
	@Autowired 
	private PollRepository pollRepo;
	
	@Autowired
	private VoteRepository voteRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	public PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size){
		validatePageNumberAndSize(page,size);
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC,"createdAt");
		Page<Poll> polls = pollRepo.findAll(pageable);
		
		if(polls.getNumberOfElements() ==0) {
			return new PagedResponse<>(Collections.emptyList(), polls.getNumber(), polls.getSize(),
					polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
		}
		
		List<Long> pollIds = polls.map(Poll::getId).getContent();
		Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
		Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);
		Map<Long, User> creatorMap = getPollCreatorMap(polls.getContent());
		
		List<PollResponse> pollResponses = polls.map(poll -> {
			return ModelMapper.mapPollToPollResponse(poll, choiceVoteCountMap, 
					creatorMap.get(poll.getCreatedBy()), 
					pollUserVoteMap==null?null:pollUserVoteMap.getOrDefault(poll.getId(), null));
		}).getContent();
		
		return new PagedResponse<PollResponse>(pollResponses, polls.getNumber(),
				polls.getSize(), polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
	}
	
	private void validatePageNumberAndSize(int page, int size) {
		if(page<0) {
			throw new BadRequestException("Page<0 is not acceptable");
		}
		if(size>AppConstants.MAX_PAGE_SIZE) {
			throw new BadRequestException("Page Limit exceeded");
		}
	}
	
	private Map<Long, Long> getChoiceVoteCountMap(List<Long> pollIds){
		List<ChoiceVoteCount> votes = voteRepo.countByPollIdInGroupByChoiceId(pollIds);
		return votes.stream().collect(Collectors.toMap(ChoiceVoteCount::getChoiceId,
				ChoiceVoteCount::getVoteCount));
	}
	
	private Map<Long, Long> getPollUserVoteMap(UserPrincipal currentUser, List<Long> pollIds){
		Map<Long, Long> pollUserVoteMap = null;
		if(currentUser!= null) {
			List<Vote> userVotes = voteRepo.findByUserIdAndPollIdIn(currentUser.getId(), pollIds);
			pollUserVoteMap = userVotes.stream()
					.collect(Collectors.toMap(vote->vote.getPoll().getId(), vote->vote.getChoice().getId()));
		}
		return pollUserVoteMap;
	}
	
	private Map<Long, User> getPollCreatorMap(List<Poll> polls){
		List<Long> creatorIds = polls.stream().map(Poll::getCreatedBy)
				.distinct().collect(Collectors.toList());
		
		List<User> creators = userRepo.findByIdIn(creatorIds);
		return creators.stream()
				.collect(Collectors.toMap(User::getId, Function.identity()));
	}
	
	public Poll createPoll(PollRequest pollRequest) {
		Poll poll = new Poll();
		poll.setQuestion(pollRequest.getQuestion());
		
		pollRequest.getChoices().forEach(choiceReq ->{
			poll.addChoice(new Choice(choiceReq.getText()));
		});
		
		Instant now = Instant.now();
		Instant expirationDate = now.plus(Duration.ofDays(pollRequest.getPollLength().getDays()))
				.plus(Duration.ofHours(pollRequest.getPollLength().getHours()));
		poll.setExpirationDate(expirationDate);
		
		return pollRepo.save(poll);
	}
	
	public PollResponse getPollById(UserPrincipal currentUser, Long pollId) {
		Poll poll = pollRepo.findById(pollId).orElseThrow(
				()-> new ResourceNotFoundException("Poll", "Id", pollId));
		
		List<ChoiceVoteCount> votes = voteRepo.countByPollIdInGroupByChoiceId(pollId);
		
		Map<Long, Long> choiceVoteMaps = votes.stream()
				.collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));
		
		User creator = userRepo.findById(poll.getCreatedBy())
				.orElseThrow(()-> new ResourceNotFoundException("User", "Id", poll.getCreatedBy()));
		
		Vote userVote=null;
		
		if(currentUser!=null) {
			userVote = voteRepo.findByUserIdAndPollIdIn(currentUser.getId(), pollId);
		}
		
		return ModelMapper.mapPollToPollResponse(poll, choiceVoteMaps, creator, 
				userVote==null?null:userVote.getChoice().getId());
	}
	
	public PollResponse castVoteAndGetUpdatedPoll(UserPrincipal currentUser, Long pollId, VoteRequest voteReq) {
		Poll poll = pollRepo.findById(pollId)
				.orElseThrow(()-> new ResourceNotFoundException("Poll", "Id", pollId));
		
		if(poll.getExpirationDate().isBefore(Instant.now())) {
			throw new BadRequestException("Sorry expired Poll");
		}
		
		User user = userRepo.getOne(currentUser.getId());
		
		Choice selectedChoice = poll.getChoices().stream()
				.filter(choice -> choice.getId().equals(voteReq.getChoiceId()))
				.findFirst()
				.orElseThrow(()-> new ResourceNotFoundException("Choice", "Id", voteReq.getChoiceId()));
		
		Vote vote = new Vote();
		vote.setUser(user);
		vote.setPoll(poll);
		vote.setChoice(selectedChoice);
		
		try {
			vote= voteRepo.save(vote);
		}catch(DataIntegrityViolationException ex) {
			logger.info("User {} has already voted in Poll {}", currentUser.getId(), pollId);
			throw new BadRequestException("Sorry multiple cast not allowed");
		}
		
		List<ChoiceVoteCount> votes = voteRepo.countByPollIdInGroupByChoiceId(pollId);
		
		Map<Long, Long> choiceVotesMap = votes.stream()
				.collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));
		
		User creator = userRepo.findById(poll.getCreatedBy())
				.orElseThrow(()-> new ResourceNotFoundException("User", "Id", poll.getCreatedBy()));
		
		return ModelMapper.mapPollToPollResponse(poll, choiceVotesMap, creator, vote.getChoice().getId());
	}
	
	public PagedResponse<PollResponse> getPollsCreatedBy(String username, 
			UserPrincipal currentUser, int page, int size){
		validatePageNumberAndSize(page, size);
		
		User user = userRepo.findByUsername(username)
				.orElseThrow(()-> new ResourceNotFoundException("User", "username", username));
		
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC,"createdAt");
		Page<Poll> polls = pollRepo.findByCreatedBy(user.getId(), pageable);
		
		if(polls.getNumberOfElements()==0) {
			return new PagedResponse<>(Collections.emptyList(),
					polls.getNumber(), polls.getSize(), polls.getTotalElements(),
					polls.getTotalPages(), polls.isLast());
		}
		
		List<Long> pollIds = polls.map(Poll::getId).getContent();
		Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
		Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);
		
		List<PollResponse> pollResponses = polls.map(poll ->{
			return ModelMapper.mapPollToPollResponse(poll, 
					choiceVoteCountMap, 
					user, 
					pollUserVoteMap==null?null:pollUserVoteMap.getOrDefault(poll.getId(), null));
		}).getContent();
		
		return new PagedResponse<>(pollResponses, polls.getNumber(), polls.getSize(),
				polls.getTotalElements(), polls.getTotalPages(), polls.isLast()
				);
	}
	
	
	public PagedResponse<PollResponse> getPollsVotedBy(String username, 
			UserPrincipal currentUser, int page, int size){
		validatePageNumberAndSize(page, size);
		
		User user = userRepo.findByUsername(username)
				.orElseThrow(()-> new ResourceNotFoundException("User", "username", username));
		
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC,"createdAt");
		Page<Long> userVotedPollsId = voteRepo.findVotedPollIdsByUserId(user.getId(), pageable);
		
		if(userVotedPollsId.getNumberOfElements()==0) {
			return new PagedResponse<>(Collections.emptyList(), userVotedPollsId.getNumber(),
					userVotedPollsId.getSize(),
					userVotedPollsId.getTotalElements(),
					userVotedPollsId.getTotalPages(),
					userVotedPollsId.isLast()
					);
		}
		
		List<Long> pollIds = userVotedPollsId.getContent();
		Sort sort = new Sort(Sort.Direction.DESC,"createdAt");
		List<Poll> polls = pollRepo.findByIdIn(pollIds, sort);
		
		Map<Long, Long> choiceCountVoteMap = getChoiceVoteCountMap(pollIds);
		Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);
		Map<Long, User> creatorMap = getPollCreatorMap(polls);
				
		List<PollResponse> pollResponses = polls.stream()
				.map(poll ->{
					return ModelMapper.mapPollToPollResponse(poll, 
							choiceCountVoteMap, 
							creatorMap.get(poll.getCreatedBy()), 
							pollUserVoteMap==null?null:pollUserVoteMap.getOrDefault(poll.getId(), null));
				}).collect(Collectors.toList());
		
		return new PagedResponse<>(pollResponses, userVotedPollsId.getNumber(),
				userVotedPollsId.getSize(),
				userVotedPollsId.getTotalElements(),
				userVotedPollsId.getTotalPages(),
				userVotedPollsId.isLast()
				);
	}
}
