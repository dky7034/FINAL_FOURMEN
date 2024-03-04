package com.itwill.teamfourmen.service;

import com.itwill.teamfourmen.domain.Member;
import com.itwill.teamfourmen.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberDao;

    public Member getMember(Long memberId){

    	Member targetMember = memberDao.findByMemberId(memberId);
    	
        Optional<Member> findMember = memberDao.findByEmail(targetMember.getEmail());

        Member targetmember = findMember.orElseThrow();

        return targetmember;
    }

}
