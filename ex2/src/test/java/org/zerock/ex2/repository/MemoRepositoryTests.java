package org.zerock.ex2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
}
