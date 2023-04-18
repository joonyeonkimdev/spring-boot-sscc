package org.zerock.ex2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.ex2.entity.Memo;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class MemoRepositoryTests {

    @Autowired
    MemoRepository memoRepository;

    @Test
    public void testClass() {
        System.out.println(memoRepository.getClass().getName());
    }

    @Test
    public void testInsertDummies() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Memo memo = Memo.builder().memoText("Sample..." + i).build();
            memoRepository.save(memo);
        });
    }

    @Test
    public void testUpdate() {
        Memo memo = Memo.builder().mno(1L).memoText("Updated...1").build();
        System.out.println("1-------------------------");
        memoRepository.save(memo);
        System.out.println("2-------------------------");
    }

    @Test
    public void testSelect() {
        Long mno = 100L; // Id값(PK값)

        Optional<Memo> result = memoRepository.findById(mno); //Optional 타입 반환
        System.out.println("===================================");
        if (result.isPresent()) {
            Memo memo = result.get();
            System.out.println(memo);
        }
    }

    @Transactional
    @Test
    public void testSelect2() {
        Long mno = 100L;

        Memo memo = memoRepository.getOne(mno); //=> deprecated => getReferenceById 사용
        System.out.println("===================================");
        System.out.println(memo);

        // getReferenceById()도 lazy loading
        // 위의 메소드 실행에서 context가 이미 만들어져서 쿼리 실행X
        Memo memo2 = memoRepository.getReferenceById(mno);
        System.out.println("===================================");
        System.out.println(memo2);
    }

    @Test
    public void testDelete() {
        Long mno = 100L;
        memoRepository.deleteById(mno);
    }

    // JPA 페이징 테스트
    @Test
    public void testPageDefault() {
        Pageable pageable = PageRequest.of(0, 10); // 1페이지, 10개씩
        Page<Memo> result = memoRepository.findAll(pageable);
        System.out.println("===================================");
        System.out.println(result);
        System.out.println("===================================");
        System.out.println("Total Pages: " + result.getTotalPages());
        System.out.println("Total Count: " + result.getTotalElements());
        System.out.println("Page Number: " + result.getNumber());
        System.out.println("Page Size: " + result.getSize());
        System.out.println("has next page?: " + result.hasNext());
        System.out.println("first page?: " + result.isFirst());
    }

    // JPA 페이징/정렬 테스트
    @Test
    public void testPageWithSort() {
        Sort sort = Sort.by("mno").descending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Memo> result = memoRepository.findAll(pageable);

        result.get().forEach(memo -> {
            System.out.println(memo);
        });

        System.out.println("===================================");
        Sort sort1 = Sort.by("mno").descending();
        Sort sort2 = Sort.by("memoText").ascending();
        Sort sortAll = sort1.and(sort2); // Sort 객체(조건) 연결 -> and 이용
        Pageable pageable2 = PageRequest.of(0, 10, sortAll);
        Page<Memo> result2 = memoRepository.findAll(pageable2);

        result2.get().forEach(memo -> {
            System.out.println(memo);
        });
    }

    // 쿼리메소드 테스트
    @Test
    public void testQueryMethod() {
        List<Memo> list = memoRepository.findByMnoBetweenOrderByMnoDesc(70L, 80L);
        for (Memo memo : list) {
            System.out.println(memo);
        }
    }

    // 쿼리메소드+Pageable 테스트
    @Test
    public void testQueryMethodWithPageable() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").descending());
        Page<Memo> result = memoRepository.findByMnoBetween(10L, 50L, pageable);
        result.get().forEach(memo -> System.out.println(memo));
    }

    // 삭제 쿼리메소드 테스트
    @Transactional // 내부적으로 SELECT와 DELETE 구문이 각각 실행됨 -> 하나의 트랜젝션으로 처리.
    @Commit // deleteBy..의 테스트 코드는 롤백이 기본 -> @Commit으로 DB반영.
    @Test
    public void testDeleteQueryMethod() {
        memoRepository.deleteMemoByMnoLessThan(10L);
    }

    // @Query 테스트
    @Test
    public void testQueryAnnotation1(){
        List<Memo> list = memoRepository.getListDesc();
        for (Memo memo : list) {
            System.out.println(memo);
        }
    }

    // @Query 파라미터 바인딩 테스트
    @Test
    public void testQueryAnnotation2() {
        int i = memoRepository.updateMemoText1(10L, "updated by @Query 1");
        System.out.println(i + " <=======================================");

        Memo memo = Memo.builder().mno(11L).memoText("updated by @Query 2").build();
        int j = memoRepository.updateMemoText2(memo);
        System.out.println(j + " <=======================================");
    }

    // @Query 페이징 테스트
    @Test
    public void testQueryAnnotation3() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").ascending());
        Page<Memo> pageResult = memoRepository.getListWithQuery(20L, pageable);
        System.out.println("======================================");
        pageResult.get().forEach(memo -> System.out.println(memo));
    }

    // @Query Object[] 리턴 테스트
    @Test
    public void testQueryAnnotation4() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("mno").ascending());
        Page<Object[]> pageResult = memoRepository.getListWithQueryObject(75L, pageable);
        System.out.println("======================================");
        pageResult.get().forEach(objectArr -> {
            for (int i = 0; i < objectArr.length; i++){
                System.out.print(objectArr[i] + " ");
            }
            System.out.println();
        });
    }

    // @Query 네이티브 쿼리 테스트
    @Test
    public void testQueryAnnotation5() {
        List<Object[]> list = memoRepository.getNativeResult();
        System.out.println("======================================");
        for (Object[] objectArr : list) {
            for (int i = 0; i < objectArr.length; i++){
                System.out.print(objectArr[i] + " ");
            }
            System.out.println();
        }
    }

}
