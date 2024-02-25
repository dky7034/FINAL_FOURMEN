package com.itwill.teamfourmen.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Table(name= "review_comments")
@SequenceGenerator(name = "review_comments_seq", sequenceName = "review_comments_seq" , allocationSize = 1)
public class ReviewComments {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_comments_seq")
    private Long commentId;

    @Basic(optional = false)
    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @Basic(optional = false)
    @ManyToOne
    @JoinColumn(name = "email")
    private Member member;

    @Basic(optional = false)
    private String content;

    private Long likes;

    @CreatedDate
    private LocalDateTime createdDate;

    public void updateComment(String content){
        this.content = content;
    }
}
