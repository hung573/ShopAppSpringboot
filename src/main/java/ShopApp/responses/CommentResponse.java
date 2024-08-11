/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.responses;

import ShopApp.models.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mac
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    @JsonProperty("product_id")
    private long productId;
    
    @JsonProperty("user_id")
    private long userId;
    
    @JsonProperty("content")
    private String content;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public static CommentResponse fromComment(Comment comment){
        CommentResponse commentResponse = CommentResponse.builder()
                .productId(comment.getProduct().getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
        return commentResponse;
    }
}
