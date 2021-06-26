package com.example.Point;

import com.example.dto.TeamMemberDto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@RedisHash("point")
@ToString
public class TeamMemberPoint implements Serializable {

    @Id
    private String id;
    private Long amount;
    private ZonedDateTime refreshTime;
    private TeamMemberDto teamMemberDto;

    @Builder
    public TeamMemberPoint(String id, Long amount, ZonedDateTime refreshTime, TeamMemberDto teamMemberDto) {
        this.id = id;
        this.amount = amount;
        this.refreshTime = refreshTime.plusSeconds(amount);
        this.teamMemberDto = teamMemberDto;
    }

    public boolean available() {
        return ZonedDateTime.now().isBefore(this.refreshTime);
    }

    public void refresh(long amount, ZonedDateTime refreshTime, TeamMemberDto teamMemberDto) {
        if (refreshTime.isAfter(this.refreshTime)) {
            this.amount = amount;
            this.refreshTime = refreshTime.plusSeconds(amount);
            this.teamMemberDto = teamMemberDto;
        }
    }
}
