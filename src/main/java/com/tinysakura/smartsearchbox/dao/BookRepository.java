package com.tinysakura.smartsearchbox.dao;

import com.tinysakura.smartsearchbox.annotation.Index;
import com.tinysakura.smartsearchbox.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/15
 */

public interface BookRepository extends JpaRepository<Book, Long> {

    @Index(index = "mediiiia", documentType = "boooook", searchPromptFields = {"author", "title", "content"}, id = "id")
    @Override
    <S extends Book> S save(S s);
}