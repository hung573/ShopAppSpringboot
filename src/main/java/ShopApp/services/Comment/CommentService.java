/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services.Comment;

import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.CommentDTO;
import ShopApp.dtos.CommentUpdateDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Comment;
import ShopApp.models.Product;
import ShopApp.models.User;
import ShopApp.repositories.CommentRepository;
import ShopApp.responses.CommentResponse;
import ShopApp.services.Product.ProductService;
import ShopApp.services.User.UserService;
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
    public Comment updateComment(long id, CommentUpdateDTO commentUpdateDTO) throws Exception {
        Comment comment = getCommentId(id);
        comment.setContent(commentUpdateDTO.getContent());
        
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
    public List<CommentResponse> getAllCommentList() throws Exception {
        List<Comment> comments = commentRepository.findAll();
    return comments.stream()
                   .map(CommentResponse::fromComment)
                   .collect(Collectors.toList());
    }

    @Override
    public Page<CommentResponse> getAlCommentByUserId(long userId, PageRequest pageRequest) throws Exception {
        User user = userService.getUserDetailFromId(userId);
        Page<Comment> comments = commentRepository.findByUserId(userId, pageRequest);
        return comments.map(CommentResponse::fromComment);
    }

    @Override
    public Page<CommentResponse> getAllCommentByProductId(long productId, PageRequest pageRequest) throws Exception {
        Page<Comment> comments = commentRepository.findByProductId(productId, pageRequest);
        return comments.map(CommentResponse::fromComment);
    }

    @Override
    public Page<CommentResponse> getAllCommentByUserIdAndProductId(long userId, long productId, PageRequest pageRequest) throws Exception {
        User user = userService.getUserDetailFromId(userId);
        Page<Comment> comments = commentRepository.findByUserIdAndProductId(userId, productId, pageRequest);
        return comments.map(CommentResponse::fromComment);
    }

    @Override
    public Page<CommentResponse> getAllCommentPage(long productId, String keyword, PageRequest pageRequest) throws Exception {
        Page<Comment> comments = commentRepository.searchComments(productId, keyword, pageRequest);
        return comments.map(CommentResponse::fromComment);
    }
    
}
