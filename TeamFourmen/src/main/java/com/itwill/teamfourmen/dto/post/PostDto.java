package com.itwill.teamfourmen.dto.post;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.itwill.teamfourmen.domain.Member;
import com.itwill.teamfourmen.domain.Post;

import jakarta.persistence.Basic;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
	
	private Long postId;

	private String email;
	
	private String title;
	
	private String content;
	
	private String category;
	
	private LocalDateTime createdTime;
	
	private LocalDateTime modifiedTime;
	
	private Long views;
	
	private Long likes;
	
	
	public Post toEntity() {
		
		return Post.builder()
				.postId(this.postId)
				.member(Member.builder().email(this.email).build())
				.title(this.title)
				.content(this.content)
				.category(this.category)
				.createdTime(this.createdTime)
				.modifiedTime(this.modifiedTime)
				.views(this.views)
				.likes(this.likes)
				.build();
	}
	
}
