/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.iservices;

import ShopApp.dtos.CommentDTO;
import ShopApp.dtos.CommentUpdateDTO;
import ShopApp.models.Comment;
import ShopApp.responses.CommentResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author mac
 */
public interface ICommentService {
    Comment createComment(CommentDTO commentDTO) throws Exception;
    
    Comment updateComment(long id, CommentUpdateDTO commentUpdateDTO) throws Exception;
    
    void deleteComment(long id) throws Exception;
    
    Comment getCommentId(long id) throws Exception;
    
    Page<CommentResponse> getAllCommentPage(long productId, String keyword, PageRequest pageRequest) throws Exception;
    
    List<CommentResponse> getAllCommentList() throws Exception;
    
    Page<CommentResponse> getAlCommentByUserId(long userId, PageRequest pageRequest) throws Exception;
    
    Page<CommentResponse> getAllCommentByProductId(long productId, PageRequest pageRequest) throws Exception;
    
    Page<CommentResponse> getAllCommentByUserIdAndProductId(long userId,long productId, PageRequest pageRequest) throws Exception;

}
