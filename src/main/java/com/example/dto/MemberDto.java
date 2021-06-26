package com.example.dto;

import com.example.entity.Member;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class MemberDto {
    private Long id;
    private String name;

    public MemberDto() {
    }

    public MemberDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
    }
}
