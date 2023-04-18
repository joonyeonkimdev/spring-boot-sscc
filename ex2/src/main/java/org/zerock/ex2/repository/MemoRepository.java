package org.zerock.ex2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ex2.entity.Memo;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    // 쿼리메소드
    List<Memo> findByMnoBetweenOrderByMnoDesc(Long from, Long to);

    // 쿼리메소드 + Pageable
    Page<Memo> findByMnoBetween(Long from, Long to, Pageable pageable);

    // 삭제 쿼리메소드
    void deleteMemoByMnoLessThan(Long num);

}
