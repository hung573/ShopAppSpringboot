/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.CommentDTO;
import ShopApp.models.Comment;
import ShopApp.responses.CommentResponse;
import ShopApp.responses.ListResponse;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.services.CommentService;
import ShopApp.utils.MessageKey;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final LocalizationUtils localizationUtils;

    
    @PostMapping("/add")
    private ResponseEntity<ObjectResponse> addComment(
            @Valid @RequestBody CommentDTO commentDTO,
            BindingResult result){
        try {
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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                        .build());
        }
    }
    
    @GetMapping("")
    private ResponseEntity<?> getAllCommentList(){
        try {
            List<CommentResponse> comment = commentService.getAllCommentList();
            return ResponseEntity.ok(ListResponse.<CommentResponse>builder()
                    .items(comment)
                    .totalPages(0)
                    .page(0)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MessageResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                        .build());
        }
    }
    
    @GetMapping("/by-product")
    private ResponseEntity<?> getAllCommentByProductId(
            @RequestParam(name = "id") long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit){
        try {
            // Điều chỉnh page để bắt đầu từ 1 thay vì 0
            int adjustedPage = page > 0 ? page - 1 : 0;
            // Tạo Pageable từ page và limit
            PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                    Sort.by("id").ascending());
            Page<CommentResponse> commentResponsePage = commentService.getAllCommentByProductId(productId,pageRequest);
            List<CommentResponse> commentResponseList = commentResponsePage.getContent();
            return ResponseEntity.ok(ListResponse.<CommentResponse>builder()
                    .items(commentResponseList)
                    .totalPages(commentResponsePage.getTotalPages())
                    .page(page)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MessageResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                        .build());
        }
    }
}
