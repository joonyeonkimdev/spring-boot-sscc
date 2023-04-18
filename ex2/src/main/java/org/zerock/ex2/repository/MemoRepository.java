package org.zerock.ex2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.ex2.entity.Memo;

import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    // 쿼리메소드
    List<Memo> findByMnoBetweenOrderByMnoDesc(Long from, Long to);

    // 쿼리메소드 + Pageable
    Page<Memo> findByMnoBetween(Long from, Long to, Pageable pageable);

    // 삭제 쿼리메소드
    void deleteMemoByMnoLessThan(Long num);

    // @Query
    @Query("select m from Memo m order by m.mno desc")
    List<Memo> getListDesc();

    // @Query 파라미터 바인딩, DML -> @Modifying
    @Transactional
    @Modifying
    @Query("update Memo m set m.memoText = :memoText where m.mno = :mno")
    int updateMemoText1(@Param("mno") Long mno, @Param("memoText") String memoText);

    // @Query 객체 파라미터 바인딩
    @Transactional
    @Modifying
    @Query("update Memo m set m.memoText = :#{#param.memoText} where m.mno = :#{#param.mno}")
    int updateMemoText2(@Param("param") Memo memo);

    // @Query 페이징
    @Query(value = "select m from Memo m where m.mno > :mno",
            countQuery = "select count(m) from Memo m where m.mno > :mno") // @Query 페이징 시 countQuery 속성 필수
    Page<Memo> getListWithQuery(Long mno, Pageable pageable);

    // @Query Object[] 리턴 -> 엔티티 프로퍼티 이외의 값 SELECT 시(JOIN 등)
    @Query(value = "select m.mno, m.memoText, CURRENT_DATE from Memo m where m.mno > :mno",
            countQuery = "select count(m) from Memo m where m.mno > :mno")
    Page<Object[]> getListWithQueryObject(Long mno, Pageable pageable);

    // @Query 네이티브 쿼리
    @Query(value = "select * from tbl_memo where mno > 0", nativeQuery = true)
    List<Object[]> getNativeResult();

}
