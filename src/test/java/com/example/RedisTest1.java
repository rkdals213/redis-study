package com.example;

import com.example.Point.Point;
import com.example.Point.TeamMemberPoint;
import com.example.dto.TeamMemberDto;
import com.example.entity.Member;
import com.example.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@Transactional
class RedisTest1 {

    @Autowired
    private PointRedisRepository pointRedisRepository;

    @Autowired
    private TeamMemberPointRedisRepository teamMemberPointRedisRepository;

    @Autowired
    private TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    void redis_설정테스트() {
        //given
        String id = "min";
        ZonedDateTime refreshTime = ZonedDateTime.now();
        Point point = Point.builder()
                .id(id)
                .amount(1000L)
                .refreshTime(refreshTime)
                .build();

        //when
        pointRedisRepository.save(point);

        //then
        Point savedPoint = pointRedisRepository.findById(id).get();
        assertThat(savedPoint.getAmount(), is(1000L));
        assertThat(savedPoint.getRefreshTime(), is(refreshTime));

        pointRedisRepository.deleteAll();
    }

    @Test
    void redis_객체저장_테스트() {
        //given
        Team team = new Team("Team1");
        em.persist(team);
        Member member1 = new Member("Member1", team);
        Member member2 = new Member("Member2", team);
        Member member3 = new Member("Member3", team);
        team.getMembers().add(member1);
        team.getMembers().add(member2);
        team.getMembers().add(member3);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);

        em.flush();
        em.clear();

        Team result = teamRepository.findById(team.getId()).get();

        String id = "team";
        ZonedDateTime refreshTime = ZonedDateTime.now();
        TeamMemberPoint point = TeamMemberPoint.builder()
                .id(id)
                .amount(1000L)
                .refreshTime(refreshTime)
                .teamMemberDto(new TeamMemberDto(result))
                .build();

        //when
        teamMemberPointRedisRepository.save(point);

        //then
        TeamMemberPoint savedPoint = teamMemberPointRedisRepository.findById(id).get();

        assertThat(savedPoint.getAmount(), is(1000L));
        assertThat(savedPoint.getTeamMemberDto().getLabel(), is("Team1"));
        assertThat(savedPoint.getTeamMemberDto().getMembers().size(), is(3));

        pointRedisRepository.deleteAll();
    }

    @Test
    void redis에서_가져온_값이_유효하면_사용하고_아니면_db에서_가져와_갱신하기() throws InterruptedException {
        //given
        Team team = new Team("Team1");
        em.persist(team);
        Member member1 = new Member("Member1", team);
        Member member2 = new Member("Member2", team);
        Member member3 = new Member("Member3", team);
        team.getMembers().add(member1);
        team.getMembers().add(member2);
        team.getMembers().add(member3);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);

        em.flush();
        em.clear();

        Team result = teamRepository.findById(team.getId()).get();

        String id = "team";
        ZonedDateTime refreshTime = ZonedDateTime.now();
        TeamMemberPoint point = TeamMemberPoint.builder()
                .id(id)
                .amount(2L)
                .refreshTime(refreshTime)
                .teamMemberDto(new TeamMemberDto(result))
                .build();

        //when
        teamMemberPointRedisRepository.save(point);
        System.out.println("=================================================================================================================================");
        Thread.sleep(5000L);

        em.flush();
        em.clear();

        //then
        TeamMemberPoint savedPoint = teamMemberPointRedisRepository.findById(id).get();

        if (savedPoint.available()) {
            assertThat(savedPoint.getAmount(), is(2L));
            assertThat(savedPoint.getTeamMemberDto().getLabel(), is("Team1"));
            assertThat(savedPoint.getTeamMemberDto().getMembers().size(), is(3));
        } else {
            Team teamFromMySql = teamRepository.findById(team.getId()).get();
            point.refresh(6L, ZonedDateTime.now(), new TeamMemberDto(teamFromMySql));
            teamMemberPointRedisRepository.save(point);

            TeamMemberPoint savedPointAfter = teamMemberPointRedisRepository.findById(id).get();
            assertThat(savedPointAfter.getAmount(), is(6L));
            assertThat(savedPointAfter.getTeamMemberDto().getLabel(), is("Team1"));
            assertThat(savedPointAfter.getTeamMemberDto().getMembers().size(), is(3));
        }

        pointRedisRepository.deleteAll();
    }
}
