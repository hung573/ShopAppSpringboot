/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.iservices;

import ShopApp.dtos.CommentDTO;
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
    
    Comment updateComment(long id, CommentDTO commentDTO) throws Exception;
    
    void deleteComment(long id) throws Exception;
    
    Comment getCommentId(long id) throws Exception;
    
    Page<Comment> getAllCommentPage(PageRequest pageRequest) throws Exception;
    
    List<CommentResponse> getAllCommentList() throws Exception;
    
    List<Comment> getAlCommentByUserId(long userId) throws Exception;
    
    Page<CommentResponse> getAllCommentByProductId(long productId, PageRequest pageRequest) throws Exception;
}
