package br.com.nlw.events.service;

import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.UserIndicadorNotFoundException;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repo.EventRepo;
import br.com.nlw.events.repo.SubscriptionRepo;
import br.com.nlw.events.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class SubscriptionService {
    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubscriptionRepo subscriptionRepo;

    public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId) {
        Event event = eventRepo.findByPrettyName(eventName);

        if (event == null) {
            throw new EventNotFoundException("Evento " + eventName + " não existe");
        }

        User userRec = userRepo.findByEmail(user.getEmail());

        if (userRec == null) {
            userRec = userRepo.save(user);
        }

        User indicador = null;

        if (userId != null) {
            indicador = userRepo.findById(userId).orElse(null);

            if (indicador == null) {
                throw new UserIndicadorNotFoundException("Usuário " + userId + "indicador não existe!");
            }
        }

        Subscription sub = new Subscription();
        sub.setEvent(event);
        sub.setSubscriber(userRec);
        sub.setIndication(indicador);

        Subscription subRec = subscriptionRepo.findByEventAndSubscriber(event, userRec);
        if (subRec != null) {
            throw new SubscriptionConflictException("Já existe inscrição para o usuário " + userRec.getName());
        }

        Subscription res = subscriptionRepo.save(sub);

        return new SubscriptionResponse(
                res.getSubscriptionNumber(),
                "http://codecraft.com/subscription" +
                res.getEvent().getPrettyName() + "/" +
                res.getSubscriber().getId()
        );
    }

    public List<SubscriptionRankingItem> getCompleteRanking(String prettyName) {
        Event event = eventRepo.findByPrettyName(prettyName);

        if (event == null) {
            throw new EventNotFoundException("Ranking do evento " + prettyName + " não existe!");
        }

        return subscriptionRepo.generateRanking(event.getEventId());
    }

    public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId) {
        List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);

        SubscriptionRankingItem item = ranking.stream()
            .filter(i -> i.usertId().equals(userId)).findFirst().orElse(null);

        if (item == null) {
            throw new UserIndicadorNotFoundException("Não há inscrições com indicação do usuário " + userId);
        }

        Integer posicao = IntStream.range(0, ranking.size())
                .filter(pos -> ranking.get(pos).usertId().equals(userId))
                .findFirst().getAsInt();

        return new SubscriptionRankingByUser(item, posicao + 1);
    }
}
