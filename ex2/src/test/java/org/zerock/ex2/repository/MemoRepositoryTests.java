package org.zerock.ex2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.ex2.entity.Memo;

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

    // JPA 페이징
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

    // JPA 페이징 + 정렬
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
}
