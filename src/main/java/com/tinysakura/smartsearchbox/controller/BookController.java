package com.tinysakura.smartsearchbox.controller;

import com.tinysakura.smartsearchbox.common.ResponseView;
import com.tinysakura.smartsearchbox.constant.enums.ResponseCodeEnum;
import com.tinysakura.smartsearchbox.dao.BookRepository;
import com.tinysakura.smartsearchbox.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/15
 */
@RestController
public class BookController {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    ApplicationContext applicationContext;

    @PostMapping(value = "/book/save")
    public ResponseView<Book> saveBook(@RequestBody Book book) {
        Book save = bookRepository.save(book);

        ResponseView responseView = new ResponseView();
        responseView.setCode(ResponseCodeEnum.OK.getCode());
        responseView.setMessage(ResponseCodeEnum.OK.getValue());
        responseView.setResult(save);

        return responseView;
    }
}