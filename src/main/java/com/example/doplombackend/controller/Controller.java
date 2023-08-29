package com.example.doplombackend.controller;

import com.example.doplombackend.model.auth.LoginData;
import com.example.doplombackend.model.auth.LoginRespond;
import com.example.doplombackend.model.clientCustomResponses.FileEntryView;
import com.example.doplombackend.model.clientCustomResponses.NewFileNameUpload;
import com.example.doplombackend.service.AuthTokenService;
import com.example.doplombackend.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public final AuthTokenService authTokenService;

    public Controller(AuthenticationManager authenticationManager,
                      FileService fileService,
                      AuthTokenService authTokenService) {
        this.authenticationManager = authenticationManager;
        this.fileService = fileService;
        this.authTokenService = authTokenService;
    }

    @PostMapping("/login")
    public LoginRespond login(@RequestBody LoginData loginData) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginData.login(),
                loginData.password()));
        return new LoginRespond(authTokenService.generateToken(authentication));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping("/file")
    public void fileUpload(MultipartFile newFile) {
        fileService.addFile(newFile);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/file")
    public ResponseEntity<byte[]> getFile(@RequestParam("fileName") String fileName) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(fileService.getFile(fileName));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/list")
    public List<FileEntryView> getList(@RequestParam("limit") int limit) {
        return fileService.getFiles(limit);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/file")
    public void updateFile(@RequestParam("fileName") String existingFileName,
                           @RequestBody NewFileNameUpload newFileName) {
        fileService.updateFile(existingFileName, newFileName);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/file")
    public void deleteFile(@RequestParam("fileName") String fileName) {
        fileService.removeFile(fileName);
    }
}
