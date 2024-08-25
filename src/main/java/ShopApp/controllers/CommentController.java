/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.CommentDTO;
import ShopApp.dtos.CommentUpdateDTO;
import ShopApp.models.Comment;
import ShopApp.models.User;
import ShopApp.responses.CommentResponse;
import ShopApp.responses.ListResponse;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.services.Comment.CommentService;
import ShopApp.services.User.UserService;
import ShopApp.utils.MessageKey;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mac
 */
@RestController
@RequestMapping("${api.prefix}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("/add")
    private ResponseEntity<?> addComment(
            @Valid @RequestBody CommentDTO commentDTO,
            BindingResult result) throws Exception {
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loginUser.getId() != commentDTO.getUserId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (result.hasErrors()) {
            List<String> errormessage = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, errormessage.get(0)))
                    .build());
        }
        Comment comment = commentService.createComment(commentDTO);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message("yes")
                .items(CommentResponse.fromComment(comment))
                .build());
    }

    @GetMapping("/list")
    private ResponseEntity<?> getAllCommentList() throws Exception {
        List<CommentResponse> comment = commentService.getAllCommentList();
        return ResponseEntity.ok(ListResponse.<CommentResponse>builder()
                .items(comment)
                .totalPages(0)
                .page(0)
                .build());
    }

    @GetMapping("/admin")
    private ResponseEntity<?> getAllCommentPage(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "product_id") Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        // Điều chỉnh page để bắt đầu từ 1 thay vì 0
        int adjustedPage = page > 0 ? page - 1 : 0;

        // Tạo Pageable từ adjustedPage và limit
        PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                Sort.by("id").ascending()
        );
        Page<CommentResponse> pageCommentResponses = commentService.getAllCommentPage(productId, keyword, pageRequest);
        // tong trang
        int totalPages = pageCommentResponses.getTotalPages();

        List<CommentResponse> listCommentResponses = pageCommentResponses.getContent();

        ListResponse<CommentResponse> commemntListResponse = ListResponse.<CommentResponse>builder()
                .items(listCommentResponses)
                .totalPages(totalPages)
                .page(page)
                .build();
        return ResponseEntity.ok(commemntListResponse);

    }

    @GetMapping("/by-product")
    private ResponseEntity<?> getAllCommentByProductId(
            @RequestParam(name = "id") long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        // Điều chỉnh page để bắt đầu từ 1 thay vì 0
        int adjustedPage = page > 0 ? page - 1 : 0;
        // Tạo Pageable từ page và limit
        PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                Sort.by("id").ascending());
        Page<CommentResponse> commentResponsePage = commentService.getAllCommentByProductId(productId, pageRequest);
        List<CommentResponse> commentResponseList = commentResponsePage.getContent();
        return ResponseEntity.ok(ListResponse.<CommentResponse>builder()
                .items(commentResponseList)
                .totalPages(commentResponsePage.getTotalPages())
                .page(page)
                .build());
    }

    @GetMapping("/by-user")
    private ResponseEntity<?> getAllCommentByUserId(
            @RequestParam("user-id") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        //kiem tra user dang nhap
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loginUser.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Điều chỉnh page để bắt đầu từ 1 thay vì 0
        int adjustedPage = page > 0 ? page - 1 : 0;
        // Tạo Pageable từ page và limit
        PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                Sort.by("id").ascending());
        Page<CommentResponse> commentResponsePage = commentService.getAlCommentByUserId(userId, pageRequest);
        List<CommentResponse> commentResponseList = commentResponsePage.getContent();
        return ResponseEntity.ok(ListResponse.<CommentResponse>builder()
                .items(commentResponseList)
                .totalPages(commentResponsePage.getTotalPages())
                .page(page)
                .build());
    }

    @GetMapping("/by-user/by-product")
    private ResponseEntity<?> getAllCommentByUserIdAndByProductId(
            @RequestParam(name = "user-id") long userId,
            @RequestParam(name = "product-id") long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) throws Exception {
        //kiem tra user dang nhap
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loginUser.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Điều chỉnh page để bắt đầu từ 1 thay vì 0
        int adjustedPage = page > 0 ? page - 1 : 0;
        // Tạo Pageable từ page và limit
        PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                Sort.by("id").ascending());
        Page<CommentResponse> commentResponsePage = commentService.getAllCommentByUserIdAndProductId(userId, productId, pageRequest);
        List<CommentResponse> commentResponseList = commentResponsePage.getContent();
        return ResponseEntity.ok(ListResponse.<CommentResponse>builder()
                .items(commentResponseList)
                .totalPages(commentResponsePage.getTotalPages())
                .page(page)
                .build());
    }

    @PutMapping("update/{id}")
    private ResponseEntity<?> updateCommentUser(
            @PathVariable("id") long commentId,
            @RequestParam(name = "user-id") long userId,
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody CommentUpdateDTO commentUpdateDTO,
            BindingResult result) throws Exception {
        String token = authorizationHeader.substring(7);
        User loginUser = userService.getUserDetailFromToken(token);
        if (loginUser.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (result.hasErrors()) {
            List<String> errormessage = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, errormessage.get(0)))
                    .build());
        }
        Comment comment = commentService.updateComment(commentId, commentUpdateDTO);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_SUCCESSFULLY))
                .items(CommentResponse.fromComment(comment))
                .build());
    }

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<ObjectResponse> deleteComment(
            @PathVariable("id") long commentId,
            @RequestParam(name = "user-id") long userId) throws Exception {
        //kiem tra user dang nhap
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loginUser.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.DELETE_SUCCESSFULLY))
                .build());
    }
}
