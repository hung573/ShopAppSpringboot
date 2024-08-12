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
public class CommentResponse extends BaseResponse{
    
    private long id;
    
    @JsonProperty("product_id")
    private long productId;
    
    @JsonProperty("user")
    private UserResponse userResponse;
    
    @JsonProperty("content")
    private String content;
    
    
    public static CommentResponse fromComment(Comment comment){
        CommentResponse commentResponse = CommentResponse.builder()
                .id(comment.getId())
                .productId(comment.getProduct().getId())
                .userResponse(UserResponse.fromUser(comment.getUser()))
                .content(comment.getContent())
                .build();
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setUpdatedAt(comment.getUpdatedAt());
        return commentResponse;
    }
}
