package com.tinysakura.smartsearchbox.entity;

import com.tinysakura.smartsearchbox.annotation.Document;
import com.tinysakura.smartsearchbox.annotation.Field;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/15
 */
@Data
@Entity
@Table(name = "book")
@EntityListeners(AuditingEntityListener.class)
@Document(indexName = "mediiiia", documentType = "boooook", searchPromptFields = {"author", "title", "content"})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Field(type = "number")
    private Long id;

    @Column(name = "author")
    @Field(type = "text", analyzer = "ik_smart")
    private String author;

    @Column(name = "title")
    @Field(type = "text", analyzer = "ik_smart")
    private String title;

    @Column(name = "content")
    @Field(type = "text", analyzer = "ik_smart")
    private String content;
}