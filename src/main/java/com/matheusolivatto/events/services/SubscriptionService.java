package com.matheusolivatto.events.services;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matheusolivatto.events.dto.SubscriptionRankingByUser;
import com.matheusolivatto.events.dto.SubscriptionRankingItem;
import com.matheusolivatto.events.dto.SubscriptionResponse;
import com.matheusolivatto.events.exceptions.EventNotFoundException;
import com.matheusolivatto.events.exceptions.SubscriptionConflictException;
import com.matheusolivatto.events.exceptions.UserIndicadorNotFoundException;
import com.matheusolivatto.events.models.Event;
import com.matheusolivatto.events.models.Subscription;
import com.matheusolivatto.events.models.User;
import com.matheusolivatto.events.repositories.EventRepo;
import com.matheusolivatto.events.repositories.SubscriptionRepo;
import com.matheusolivatto.events.repositories.UserRepo;

@Service
public class SubscriptionService {

	@Autowired
	private EventRepo evtRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private SubscriptionRepo subRepo;

	public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId) {

		// recuperar o evento pelo nome
		Event evt = evtRepo.findByPrettyName(eventName);
		if (evt == null) { // caso alternativo 2
			throw new EventNotFoundException("Evento " + eventName + " não existe");
		}
		User userRec = userRepo.findByEmail(user.getEmail());
		if (userRec == null) { // caso alternativo 1
			userRec = userRepo.save(user);
		}

		User indicador = null;
		if (userId != null) {
			indicador = userRepo.findById(userId).orElse(null);
			if (indicador == null) {
				throw new UserIndicadorNotFoundException("Usuário " + userId + " indicador não existe");
			}
		}

		Subscription subs = new Subscription();
		subs.setEvent(evt);
		subs.setSubscriber(userRec);
		subs.setIndication(indicador);

		Subscription tmpSub = subRepo.findByEventAndSubscriber(evt, userRec);
		if (tmpSub != null) { // caso alternativo 3
			throw new SubscriptionConflictException(
					"Já existe incrição para o usuário " + userRec.getName() + " no evento " + evt.getTitle());
		}

		Subscription res = subRepo.save(subs);
		return new SubscriptionResponse(res.getSubscriptionNumber(), "http://codecraft.com/subscription/"
				+ res.getEvent().getPrettyName() + "/" + res.getSubscriber().getId());
	}

	public List<SubscriptionRankingItem> getCompleteRanking(String prettyName) {
		Event evt = evtRepo.findByPrettyName(prettyName);
		if (evt == null) {
			throw new EventNotFoundException("Ranking do evento " + prettyName + " não existe");
		}
		return subRepo.generateRanking(evt.getEventId());
	}

	public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId) {
		List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);
		SubscriptionRankingItem item = ranking.stream().filter(i -> i.userId().equals(userId)).findFirst().orElse(null);
		if (item == null) {
			throw new UserIndicadorNotFoundException("Não há inscrições com indicação do usuário " + userId);
		}
		Integer posicao = IntStream.range(0, ranking.size()).filter(pos -> ranking.get(pos).userId().equals(userId))
				.findFirst().getAsInt();
		return new SubscriptionRankingByUser(item, posicao + 1);
	}

}
