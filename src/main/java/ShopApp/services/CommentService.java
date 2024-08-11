/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services;

import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.CommentDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.iservices.ICommentService;
import ShopApp.models.Comment;
import ShopApp.models.Product;
import ShopApp.models.User;
import ShopApp.repositories.CommentRepository;
import ShopApp.responses.CommentResponse;
import ShopApp.utils.MessageKey;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService{
    
    private final CommentRepository commentRepository;
    private final LocalizationUtils localizationUtils;
    private final ProductService productService;
    private final UserService userService;

    
    @Override
    @Transactional
    public Comment createComment(CommentDTO commentDTO) throws Exception {
        User user = userService.getUserDetailFromId(commentDTO.getUserId());
        Product product = productService.getProductById(commentDTO.getProductId());
        Comment newComment = Comment.builder()
                .product(product)
                .user(user)
                .content(commentDTO.getContent())
                .build();
        return commentRepository.save(newComment);
    }

    @Override
    @Transactional
    public Comment updateComment(long id, CommentDTO commentDTO) throws Exception {
        Comment comment = getCommentId(id);
        Product product = productService.getProductById(commentDTO.getProductId());
        User user = userService.getUserDetailFromId(commentDTO.getUserId());
        
        comment.setContent(commentDTO.getContent());
        
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(long id) throws Exception {
         // xoa cung
        Comment comment = getCommentId(id);
        commentRepository.delete(comment);
    }

    @Override
    public Comment getCommentId(long id) throws Exception {
        return commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
    }

    @Override
    public Page<Comment> getAllCommentPage(PageRequest pageRequest) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<CommentResponse> getAllCommentList() throws Exception {
        List<Comment> comments = commentRepository.findAll();
    return comments.stream()
                   .map(CommentResponse::fromComment)
                   .collect(Collectors.toList());
    }

    @Override
    public List<Comment> getAlCommentByUserId(long userId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Page<CommentResponse> getAllCommentByProductId(long productId, PageRequest pageRequest) throws Exception {
        Page<Comment> comments = commentRepository.findByProductId(productId, pageRequest);
        return comments.map(CommentResponse::fromComment);
    }
    
}
