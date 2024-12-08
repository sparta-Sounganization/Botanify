package com.sounganization.botanify.domain.community.repository;

import com.sounganization.botanify.domain.community.entity.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //게시글의 댓글 조회
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.childComments WHERE c.post.id = :postId")
    List<Comment> findCommentsWithChildrenByPostId(@Param("postId") Long postId);

}
