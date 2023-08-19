package com.example.doplombackend.controller;

import com.example.doplombackend.model.auth.LoginData;
import com.example.doplombackend.model.auth.LoginRespond;
import com.example.doplombackend.model.clientCustomResponses.FileEntryView;
import com.example.doplombackend.model.clientCustomResponses.NewFileNameUpload;
import com.example.doplombackend.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/")
public class Controller {
    public final AuthenticationManager authenticationManager;
    public final FileService fileService;

    public Controller(AuthenticationManager authenticationManager, FileService fileService) {
        this.authenticationManager = authenticationManager;
        this.fileService = fileService;
    }

    @PostMapping("/login")
    public LoginRespond login(@RequestBody LoginData loginData) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginData.login(),
                loginData.password()));
        return new LoginRespond(authentication.toString());
    }

    @PostMapping("/file")
    public void fileUpload(MultipartFile newFile) {
        fileService.addFile(newFile);
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> getFile(@RequestParam("fileName") String fileName) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(fileService.getFile(fileName));
    }

    @GetMapping("/list")
    public List<FileEntryView> getList(@RequestParam("limit") int limit) {
        return fileService.getFiles(limit);
    }

    @PutMapping("/file")
    public void updateFile(@RequestParam("fileName") String existingFileName, @RequestBody NewFileNameUpload newFileName) {
        fileService.updateFile(existingFileName, newFileName);
    }

    @DeleteMapping("/file")
    public void deleteFile(@RequestParam("fileName") String fileName) {
        fileService.removeFile(fileName);
    }
}
