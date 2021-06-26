package com.example;

import com.example.Point.Point;
import com.example.Point.TeamMemberPoint;
import org.springframework.data.repository.CrudRepository;

public interface TeamMemberPointRedisRepository extends CrudRepository<TeamMemberPoint, String> {
}
