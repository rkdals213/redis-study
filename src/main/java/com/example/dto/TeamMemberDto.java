package com.example.dto;

import com.example.entity.Team;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
public class TeamMemberDto {
    private Long id;
    private String label;
    List<MemberDto> members;

    public TeamMemberDto() {
    }

    public TeamMemberDto(Team team) {
        this.id = team.getId();
        this.label = team.getLabel();
        this.members = team.getMembers().stream().map(MemberDto::new).collect(Collectors.toList());
    }
}
