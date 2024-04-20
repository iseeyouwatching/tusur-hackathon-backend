package ru.hits.tusurhackathon.comparator;

import ru.hits.tusurhackathon.entity.ProposalEntity;

import java.util.Comparator;

public class ProposalComparator implements Comparator<ProposalEntity> {
    @Override
    public int compare(ProposalEntity proposal1, ProposalEntity proposal2) {
        // Сравниваем суммы голосов "за" и "против"
        int totalVotes1 = proposal1.getVotesFor() + proposal1.getVotesAgainst();
        int totalVotes2 = proposal2.getVotesFor() + proposal2.getVotesAgainst();

        // Сравниваем по убыванию суммы голосов
        return Integer.compare(totalVotes2, totalVotes1);
    }
}
